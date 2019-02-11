#include <SoftwareSerial.h>
#include <UIPEthernet.h>

// CONFIG:
const int ARDUINO_ID = 3;                 // ID of the particular arduino you're flashing this on - TWO DIFFERENT ARDUINOS SHOULDN'T HAVE THE SAME ID
const long PHONE_NUM_1 = 950626299;       // add as many phone numbers as needed - don't forget to add duplicate and edit rows where they're used too
const int POWER_OUTAGE_PIN = 2;           // digital pin to which power outage detector is connected
const int OK_MESSAGE_FREQUENCY = 10000;   // how often (in milliseconds) should arduino send message to backend about its status

// Configure software serial port
SoftwareSerial SIM900(7, 8);

// Configure UDP
EthernetUDP udp;

// Helper variables
unsigned long next;
boolean errMessageSent = false;
boolean powerOutageMessageSent = false;

void setup() {
  /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
  // Serial init for debugging
  Serial.begin(19200);
  /*!!!DOUBLE CHECK YOUR SERIAL MONITOR BAUD IS SET TO 19200 - NECESSITY BECAUSE OF THE SIM900 MODULE!!!*/
  
  // GSM init for SMS notifications
  SIM900.begin(19200);
  
  // Check if GSM init was successful - debugging only
  gsmCheck();

  // Ethernet init
  byte mac[] = {0x41, 0x52, 0x44, 0x55, 0x4E, 0xA0 + ARDUINO_ID}; // arduino id is coded into mac, so it's unique and identifiable
  Ethernet.begin(mac, IPAddress(147, 213, 232, 140 + ARDUINO_ID));
  udp.begin(5000);
}

void loop() {
  // If power outage is happening, keep trying to send SMS, when it's sent, do nothing
  // If power outage isn't happening, wait for notification requests and check GSM status
  if (digitalRead(POWER_OUTAGE_PIN) == HIGH && !powerOutageMessageSent) {
    Serial.println("Power outage!");
    if (gsmCheck()) {
      Serial.println("Sending SMS!");
      sendSMS(PHONE_NUM_1, "POZOR: Vypadok elektriny v laboratoriu!");
      powerOutageMessageSent = true;
    }
  } else if (digitalRead(POWER_OUTAGE_PIN) == LOW) {
    // helper vars setup
    powerOutageMessageSent = false;
    int success;
    next = millis() + OK_MESSAGE_FREQUENCY; // new timeout
  
    // Try to receive packet
    Serial.println("Waiting for packet...");
    do {
        //check for new udp-packet:
        success = udp.parsePacket();
    } while (!success && (millis()<next) && next - millis() <= OK_MESSAGE_FREQUENCY);
  
    // If packet was received, process it
    if (success){
      char* msg = (char*)malloc(success+1);
      int len = udp.read(msg,success+1);
      msg[len]=0;
      
      udp.flush();
  
      Serial.println("Received message over ethernet:");
      Serial.println(msg);
  
      Serial.println("Sending SMS...");
      sendSMS(PHONE_NUM_1, msg);
    }/* else {
      Serial.println("Did not receive anything.");
    }*/
  
    // Check if GSM communication is functional and report to backend
    if (gsmCheck()) {
      char msg[7];
      sprintf(msg, "I%dA1S0", ARDUINO_ID);
      Serial.print("Sending packet ");
      Serial.println(msg);
      sendPacket(msg);
      errMessageSent = false;
    } else if (!errMessageSent) {
      Serial.println("GSM down");
      char msg[7];
      sprintf(msg, "I%dA1S1", ARDUINO_ID);
      Serial.print("Sending packet ");
      Serial.println(msg);
      sendPacket(msg);
      errMessageSent = true;
    }
  
    Serial.println("**********");
  }
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
  Serial.println("SMS sent");
}

void sendPacket(char* message) {
  int success;
  
  do {
    Serial.println("Trying to create packet...");
    success = udp.beginPacket(IPAddress(147, 213, 232, 125), 5000);
  } while (!success);
  Serial.println("Packet created...");

   udp.write(message);
   Serial.println("Packet sent");
   udp.endPacket();
   udp.stop();
   Serial.println("Packet object destroyed");
}

bool gsmCheck() {
  Serial.println("GSM check");
  SIM900.println("AT");
  delay(100);
  SIM900.println((char)26);
  delay(100);

  Serial.println("Command sent...");

  if(SIM900.available() > 0) {
    String message = SIM900.readString();
    //Serial.println(message);

    if (message.charAt(6) == 'O' && message.charAt(7) == 'K') {
      Serial.println("GSM available");
      return true;
    }
  }
  
  Serial.println("GSM offline");
  return false;
}

