#include <SoftwareSerial.h>
#include <UIPEthernet.h>

// CONFIG:
int okMessageFrequency = 10000;
int arduino_id = 3;

// Configure software serial port
SoftwareSerial SIM900(7, 8);

// Configure UDP
EthernetUDP udp;
unsigned long next;

void setup() {
  Serial.begin(19200);
  SIM900.begin(19200);
  // Give time to your GSM shield log on to network
  delay(20000);
  gsmCheck();

  uint8_t mac[6] = { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60 };
  Ethernet.begin(mac, IPAddress(147, 213, 232, 143));
  next = millis() + okMessageFrequency;
  udp.begin(5000);
}

void loop() {
  int success;

  Serial.println("Waiting for packet...");
  do {
      //check for new udp-packet:
      success = udp.parsePacket();
  } while (!success && (millis()<next) && next - millis() <= okMessageFrequency);
   
  if (success){
    char* msg = (char*)malloc(success+1);
    int len = udp.read(msg,success+1);
    msg[len]=0;
    
    udp.flush();

    Serial.println("Received message over ethernet:");
    Serial.println(msg);

    Serial.println("Sending SMS...");
    sendSMS(950626299, msg);
  } else {
    Serial.println("Failed to receive!");
  }

  if (gsmCheck()) {
    char msg[7];
    sprintf(msg, "I%dA1S0", arduino_id);
    Serial.print("Sending packet ");
    Serial.println(msg);
    sendPacket(msg);
  } else {
    Serial.println("GSM down");
    char msg[7];
    sprintf(msg, "I%dA1S1", arduino_id);
    Serial.print("Sending packet ");
    Serial.println(msg);
    sendPacket(msg);
  }

  Serial.println("**********");
  next = millis() + okMessageFrequency;
}

void sendSMS(long phoneNumber, char* text) {
  // AT command to set SIM900 to SMS mode
  SIM900.print("AT+CMGF=1\r"); 
  delay(100);

  // REPLACE THE X's WITH THE RECIPIENT'S MOBILE NUMBER
  // USE INTERNATIONAL FORMAT CODE FOR MOBILE NUMBERS
  String number = "AT + CMGS = \"+421";
  number += phoneNumber;
  number += "\"";
  
  SIM900.println(number);
  delay(100);
  
  // REPLACE WITH YOUR OWN SMS MESSAGE CONTENT
  SIM900.println(text);
  delay(100);

  // End AT command with a ^Z, ASCII code 26
  SIM900.println((char)26);
  delay(100);
  SIM900.println();
  // Give module time to send SMS
  delay(5000); 
}

void sendPacket(char* message) {
  int success;
  
  do {
    success = udp.beginPacket(IPAddress(147, 213, 232, 125), 5000);
  } while (!success);

   udp.write(message);
   udp.endPacket();
   udp.stop();
}

bool gsmCheck() {
  Serial.println("GSM check");
  SIM900.println("AT");
  delay(100);
  SIM900.println((char)26);
  delay(100);

  Serial.println("Command sent...");

  if(SIM900.available() > 0) {
    //Serial.println(SIM900.readString());
    Serial.println("GSM available");
    return true; 
  } else {
    Serial.println("GSM offline");
    return false;
  }
}

