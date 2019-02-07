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
  uint8_t mac[6] = {0x00,0x01,0x02,0x03,0x04,0x05};

  Ethernet.begin(mac,IPAddress(192,168,1,112));
}

void loop() {
  int success;
  
  do
    {
      success = udp.beginPacket(IPAddress(192,168,1,106),5555);
    }
  while (!success);
  
  if (success)
    {
    success = udp.write("I0A0I01T1V12.345");
    success = udp.endPacket();
    }

  delay(1000);
}
