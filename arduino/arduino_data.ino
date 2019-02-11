#include "DHT.h"
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <OneWire.h>
#include <DallasTemperature.h>

/************************CONFIG**************************************************/
int ARDUINO_ID = 0;
const int WAIT_PERIOD = 200;
/************************DHT22 sensor********************************************/
//define DHT22
const byte DHTPIN = 7;     // what pin is the DHT22 sensor connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302)

DHT dht(DHTPIN, DHTTYPE);
/************************DS18 sensor*********************************************/
const byte DS18_PIN_1 = 9;      // what pin is the 1st DS18 sensor connected to
const byte DS18_PIN_2 = 10;     // what pin is the 2nd DS18 sensor connected to

OneWire ow_ds18_1(DS18_PIN_1);
OneWire ow_ds18_2(DS18_PIN_2);

DallasTemperature ds18_1(&ow_ds18_1);
DallasTemperature ds18_2(&ow_ds18_2);
/************************Coolant sensor******************************************/
const byte COOLANT_SENSOR_PIN = A0;           // what analog pin is the coolant level sensor connected to
const int COOLANT_LEVEL_THRESHOLD = 200;      // minimum value that's considered as okay
/************************Door sensors********************************************/
const byte DOOR_SWITCH_PIN_1 = -1/*4*/;     // what pin is the front door switch connected to, -1 for disconnected
const byte DOOR_SWITCH_PIN_2 = 5;     // what pin is the back door switch connected to, -1 for disconnected
/********************************************************************************/

// Helper variables
char data_header[16];

// Ethernet variables
byte mac[] = {0x41, 0x52, 0x44, 0x55, 0x4E, 0xA0 + ARDUINO_ID}; // arduino id is coded into mac, so it's unique and identifiable
IPAddress ip(147, 213, 232, 140 + ARDUINO_ID);
IPAddress gateway(147, 213, 232, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress nameserver(147, 213, 1, 1);
unsigned int localPort = 5000;
IPAddress remoteIP(147, 213, 232, 125);
unsigned int remotePort = 5000;
EthernetUDP Udp;

void setup() {
  //Serial for debugging
  Serial.begin(9600);
  Serial.println("Initializing...");

  // Ethernet init
  Serial.println("Ethernet initializing...");
  Ethernet.begin(mac, ip, nameserver, gateway, subnet);
  Udp.begin(localPort);

  // DHT22 init
  Serial.println("DHT22 initializing...");
  dht.begin();

  // DS18 init
  Serial.println("DS18 initializing...");
  ds18_1.setWaitForConversion(true);
  ds18_1.begin();

  ds18_2.setWaitForConversion(true);
  ds18_2.begin();

  // Door switches init
  Serial.println("Door switches initializing...");

  if (DOOR_SWITCH_PIN_1 != -1) {
    pinMode(DOOR_SWITCH_PIN_1, INPUT_PULLUP);
  }

  if (DOOR_SWITCH_PIN_2 != -1) {
    pinMode(DOOR_SWITCH_PIN_2, INPUT_PULLUP);
  }

  Serial.println("Initialization finished!");
}

void loop() {
  double temp = -1;
  double hum = -1;
  byte value = -1;

  /********DHT********/
  Serial.println("********DHT********");

  temp = dht.readTemperature();
  if ( !is_NaN(temp)) {
    set_header(20, 1);
    sendData(data_header, temp);
    Serial.print("Temperature DHT "); Serial.println(temp);
  }

  hum = dht.readHumidity();
  if ( !is_NaN(hum)) {
    set_header(21, 1);
    sendData(data_header, hum);
    Serial.print("Humidity DHT "); Serial.println(hum);
  }

  /********DS18********/
  Serial.println("********DS18********");

  ds18_1.requestTemperatures();
  temp = ds18_1.getTempCByIndex(0);
  if (temp != DEVICE_DISCONNECTED_C) {
    set_header(22, 1);
    sendData(data_header, temp);
    Serial.print("Temperature DS18 1 "); Serial.println(temp);
  }

  ds18_2.requestTemperatures();
  temp = ds18_2.getTempCByIndex(0);
  if (temp != DEVICE_DISCONNECTED_C) {
    set_header(23, 1);
    sendData(data_header, temp);
    Serial.print("Temperature DS18 2 "); Serial.println(temp);
  }

  /********Coolant level********/
  Serial.println("********Coolant level********");

  value = -1;
  if (analogRead(COOLANT_SENSOR_PIN) > COOLANT_LEVEL_THRESHOLD) {
    value = 0;
    Serial.println("Coolant level is OK");
  } else {
    value = 1;
    Serial.println("Coolant level is LOW!");
  }
  set_header(24, 0);
  sendData(data_header, value);

  /********Door switches********/
  Serial.println("********Door switches********");

  if (DOOR_SWITCH_PIN_1 != -1) {
    value = -1;
    if (digitalRead(DOOR_SWITCH_PIN_1) == LOW) {
      value = 0;
      Serial.println("Front door is closed");
    } else {
      value = 1;
      Serial.println("Front door is open");
    }
    set_header(25, 0);
    sendData(data_header, value);
  }

  if (DOOR_SWITCH_PIN_2 != -1) {
    value = -1;
    if (digitalRead(DOOR_SWITCH_PIN_2) == LOW) {
      value = 0;
      Serial.println("Back door is closed");
    } else {
      value = 1;
      Serial.println("Back door is open");
    }
    set_header(26, 0);
    sendData(data_header, value);
  }

  /*********************/
  Serial.println("*********************");
  delay(WAIT_PERIOD); // wait to prevent unnecessary network load
}

int sendData(char* header, double value) {
  Udp.beginPacket(remoteIP, remotePort);
  strcat(header, String(value, 1).c_str());
  Udp.println(header);
  Udp.endPacket();

  Serial.println(header);
}

void set_header(int sensor_id, int value_type) {
  sprintf(data_header, "I%dA%dS%02dT%dV", ARDUINO_ID, 0, sensor_id, value_type);
}

bool is_NaN(double value) {
  if (value != value) return true;
  return false;
}
