#include "DHT.h"
#include <Ethernet.h>
#include <EthernetUdp.h>

/************************CONFIG**************************************************/
int wait_period = 500;
int arduino_id = 1;
/************************DHT22 sensor********************************************/
//define DHT22
#define DHTPIN_1 8     // what pin we're connected to
#define DHTPIN_2 9     // what pin we're connected to
#define DHTPIN_3 7     // what pin we're connected to
#define DHTPIN_4 6     // what pin we're connected to
#define DHTTYPE DHT22   // DHT 22  (AM2302)

DHT dht1(DHTPIN_1, DHTTYPE);
DHT dht2(DHTPIN_2, DHTTYPE);
DHT dht3(DHTPIN_3, DHTTYPE);
DHT dht4(DHTPIN_4, DHTTYPE);
/*******************************************************************************/

char data_header[16];

byte mac[] = {0xAA, 0xAA, 0xAE, 0xAA, 0xAA, 0xAA};
IPAddress ip(147, 213, 232, 141);
IPAddress gateway(147, 213, 232, 1);
IPAddress subnet(255, 255, 255,0);
IPAddress nameserver(147, 213, 1, 1);
unsigned int localPort = 5000;

IPAddress remoteIP(147, 213, 232, 125);
unsigned int remotePort = 5000;

EthernetUDP Udp;

void setup() {
  Serial.begin(9600);
  Serial.println("Initializing....");

  Ethernet.begin(mac, ip, nameserver, gateway, subnet);
  Udp.begin(localPort);

  dht1.begin();
  dht2.begin();

  Serial.println("setup finished!");
  Serial.println(" ");
}

void loop() {
  double temp;
  double hum;

  /********DHT 1********/
  temp = dht1.readTemperature();
  if( !is_NaN(temp)) {
    set_header(1,0,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht1.readHumidity();
  if( !is_NaN(hum)) {
    set_header(1,1,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  /********DHT 2********/
  temp = dht2.readTemperature();
  if( !is_NaN(temp)) {
    set_header(1,2,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht2.readHumidity();
  if( !is_NaN(hum)) {
    set_header(1,3,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  /********DHT 3********/
  temp = dht3.readTemperature();
  if( !is_NaN(temp)) {
    set_header(1,4,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht3.readHumidity();
  if( !is_NaN(hum)) {
    set_header(1,5,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  /********DHT 4********/
  temp = dht4.readTemperature();
  if( !is_NaN(temp)) {
    set_header(1,6,1);
    sendData(data_header, temp);
    Serial.print("Temperature "); Serial.println(temp);
  }
  
  hum = dht4.readHumidity();
  if( !is_NaN(hum)) {
    set_header(1,7,1);
    sendData(data_header, hum);
    Serial.print("Humidity "); Serial.println(hum);
  }
  /*********************/

  delay(wait_period);
}

int sendData(char* header, double value) {
    Udp.beginPacket(remoteIP, remotePort);
    strcat(header, String(value, 1).c_str());
    Udp.println(header);
    Udp.endPacket();
    
    Serial.println(header);
}

void set_header(int arudino_id, int sensor_id, int value_type) {
  sprintf(data_header, "I%dA%dS%02dT%dV", arudino_id, 0, sensor_id, value_type);
}

bool is_NaN(double value) {
  if(value != value) return true;
  return false;
}
