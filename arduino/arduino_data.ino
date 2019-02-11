#include "DHT.h"
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <OneWire.h> 
#include <DallasTemperature.h>

/************************CONFIG**************************************************/
int ARDUINO_ID = 0;
const int WAIT_PERIOD = 500;
/************************DHT22 sensor********************************************/
//define DHT22
const byte DHTPIN = 7;     // what pin is the DHT22 sensor connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302)

DHT dht(DHTPIN, DHTTYPE);
/************************DS18 sensor*********************************************/
const byte DS18_PIN_1 = 9;
const byte DS18_PIN_2 = 10;

OneWire ow_ds18_1(DS18_PIN_1);
OneWire ow_ds18_2(DS18_PIN_2);

DallasTemperature ds18_1(&ow_ds18_1);
DallasTemperature ds18_2(&ow_ds18_2);
/********************************************************************************/

// Helper variables
char data_header[16];

// Ethernet variables
byte mac[] = {0x41, 0x52, 0x44, 0x55, 0x4E, 0xA0 + ARDUINO_ID}; // arduino id is coded into mac, so it's unique and identifiable
IPAddress ip(147, 213, 232, 141);
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

  Serial.println("Initializing finished!");
}

void loop() {
  double temp;
  double hum;

  /********DHT********/
  Serial.println("********DHT********");
  
  temp = dht.readTemperature();
  if( !is_NaN(temp)) {
    set_header(20, 1);
    sendData(data_header, temp);
    Serial.print("Temperature DHT "); Serial.println(temp);
  }
  
  hum = dht.readHumidity();
  if( !is_NaN(hum)) {
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
  if(value != value) return true;
  return false;
}
