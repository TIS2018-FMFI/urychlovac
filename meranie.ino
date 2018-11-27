#include <SimpleTimer.h>
#include <SPI.h>
#include <Wire.h>

const int NUMBEROFDIGITALINPUTS = 5;

SimpleTimer timer;
double voltageA0 = 0;
String toPrintD;
boolean digitalInputs[NUMBEROFDIGITALINPUTS];
String digitalInputsNames[NUMBEROFDIGITALINPUTS] = {"dvere", "napatie", "plyn", "hladina", "vypadok"};

void setup() {
  Serial.begin(9600, SERIAL_8N1); //nastavenie seriovej komunikacie
  //kazdych 100ms
  timer.setInterval(100, sendA0);
  timer.setInterval(100, sendD);
}

void loop() {
  double Vcc = readVcc()/1000.0;

  int rawValue = analogRead(A0);
  voltageA0 = (rawValue / 1024.0) * Vcc;
  //v premennej voltageA0 teraz mame presnu hodnotu napatia

  //precitanie digitalnych hodnot
  for (int i = 0; i < NUMBEROFDIGITALINPUTS; i++) {
    digitalInputs[i] = (digitalRead(i) == HIGH);
  }

  timer.run();
}

void sendA0() {
  String toPrintA0 = String(voltageA0, 2); //zachovame dve desatinne miesta
  toPrintA0 = "f;teplota;" + toPrintA0 + ";\n";
  Serial.print(toPrintA0);
}

void sendD() {
  String toPrintD = "";
  for (int i = 0; i < NUMBEROFDIGITALINPUTS; i++) {
    toPrintD += "b;" + digitalInputsNames[i] + ";";
    if (digitalInputs[i] == true) {
      toPrintD += "1;\n";
    } else {
      toPrintD += "0;\n";
    }
  }
  Serial.print(toPrintD);
}

/*Sources: 
https://web.archive.org/web/20150218055034/http://code.google.com:80/p/tinkerit/wiki/SecretVoltmeter
https://hackingmajenkoblog.wordpress.com/2016/02/01/making-accurate-adc-readings-on-the-arduino/
*/
// Presne zmera referencne napatie
long readVcc() {
  long result;
  // Read 1.1V reference against AVcc
  ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  delay(2); // Wait for Vref to settle
  ADCSRA |= _BV(ADSC); // Convert
  while (bit_is_set(ADCSRA,ADSC));
  result = ADCL;
  result |= ADCH<<8;
  result = 1125300L / result; // Back-calculate AVcc in mV
  return result;
}
