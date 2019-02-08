/*
 * UIPEthernet UdpClient example.
 *
 * UIPEthernet is a TCP/IP stack that can be used with a enc28j60 based
 * Ethernet-shield.
 *
 * UIPEthernet uses the fine uIP stack by Adam Dunkels <adam@sics.se>
 *
 *      -----------------
 *
 * This UdpClient example tries to send a packet via udp to 192.168.0.1
 * on port 5000 every 5 seconds. After successfully sending the packet it
 * waits for up to 5 seconds for a response on the local port that has been
 * implicitly opened when sending the packet.
 *
 * Copyright (C) 2013 by Norbert Truchsess (norbert.truchsess@t-online.de)
 */

#include <UIPEthernet.h>

EthernetUDP udp;
unsigned long next;

void setup() {
  Serial.begin(9600);
  
  uint8_t mac[6] = { 0x10, 0x20, 0x30, 0x40, 0x50, 0x60 };
  Ethernet.begin(mac/*,IPAddress(192,168,1,180)*/);
  next = millis()+5000;
  udp.begin(5000);
}

void loop() {
  int success;
  int len = 0;
  
   do
    {
      //check for new udp-packet:
      success = udp.parsePacket();
    }
   while (!success && (millis()<next));
   
  if (success){
    char* msg = (char*)malloc(success+1);
    int len = udp.read(msg,success+1);
    msg[len]=0;

    Serial.println(msg);
    
    udp.flush();
  } else {
    Serial.println("Failed to receive!");
  }

  next = millis()+5000;
}
