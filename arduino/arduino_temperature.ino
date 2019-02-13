#include "DHT.h"
#include <Ethernet.h>
#include <EthernetUdp.h>

/************************CONFIG**************************************************/
int ARDUINO_ID = 2;
const int WAIT_PERIOD = 50;
/************************DHT22 sensor********************************************/
//define DHT22
const byte DHTPIN_1 = 5;     // what pin is the 1st sensor connected to
const byte DHTPIN_2 = 6;     // what pin is the 2nd sensor connected to
const byte DHTPIN_3 = 7;     // what pin is the 3rd sensor connected to
const byte DHTPIN_4 = 8;     // what pin is the 4th sensor connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302)

DHT dht1(DHTPIN_1, DHTTYPE);
DHT dht2(DHTPIN_2, DHTTYPE);
DHT dht3(DHTPIN_3, DHTTYPE);
DHT dht4(DHTPIN_4, DHTTYPE);
/*******************************************************************************/

// Helper variables
char data_header[16];

// Ethernet variables
byte mac[] = {0x10, 0x20, 0x30, 0x40, 0x50, 0x62}; // arduino id is coded into mac, so it's unique and identifiable
IPAddress ip(147, 213, 232, 142);
IPAddress gateway(147, 213, 232, 1);
IPAddress subnet(255, 255, 255,0);
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
  Serial.println("Ethernet init...");
  Ethernet.begin(mac, ip, nameserver, gateway, subnet);
  delay(15000);

  Serial.println("Udp init...");
  if(!Udp.begin(localPort)) {
    Serial.println("UDP failed...");
  }

  delay(15000);

  Serial.println("DHT sensors init...");
  // Sensors init
  dht1.begin();
  dht2.begin();
  dht3.begin();
  dht4.begin();

  Serial.println("Init finished!");
}

void loop() {
  double temp;
  double hum;

  /********DHT 1********/
  Serial.println("********DHT 1********");
  
  temp = dht1.readTemperature();
  if( !is_NaN(temp)) {
    set_header(8,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht1.readHumidity();
  if( !is_NaN(hum)) {
    set_header(9,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  /********DHT 2********/
  Serial.println("********DHT 2********");
  
  temp = dht2.readTemperature();
  if( !is_NaN(temp)) {
    set_header(10,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht2.readHumidity();
  if( !is_NaN(hum)) {
    set_header(11,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  /********DHT 3********/
  Serial.println("********DHT 3********");
  
  temp = dht3.readTemperature();
  if( !is_NaN(temp)) {
    set_header(12,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht3.readHumidity();
  if( !is_NaN(hum)) {
    set_header(13,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  /********DHT 4********/
  Serial.println("********DHT 4********");
  
  temp = dht4.readTemperature();
  if( !is_NaN(temp)) {
    set_header(14,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht4.readHumidity();
  if( !is_NaN(hum)) {
    set_header(15,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  Serial.println("*********************");
  //delay(WAIT_PERIOD); // wait to prevent unnecessary network load
}

void sendData(char* header, double value) {
    Udp.beginPacket(remoteIP, remotePort);
    strcat(header, String(value, 1).c_str());
    Udp.println(header);
    Udp.endPacket();
    
    Serial.println(header);
    delay(WAIT_PERIOD);
}

void set_header(int sensor_id, int value_type) {
  sprintf(data_header, "I%dA%dS%02dT%dV", ARDUINO_ID, 0, sensor_id, value_type);
}

boolean is_NaN(double value) {
  if(value != value) return true;
  return false;
}
