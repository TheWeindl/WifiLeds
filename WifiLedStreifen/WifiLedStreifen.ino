#include <ESP8266WiFi.h>
#include <WiFiClient.h>

// Hardcode WiFi parameters as this isn't going to be moving around.
const char* ssid = "PBS-370351";
const char* password = "RVLO1QB9K8XWAw8Xk34S9Iv9";

// Start a TCP Server on port 5045
WiFiServer server(5045);

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid,password);
  Serial.println("");
  //Wait for connection
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected to "); Serial.println(ssid);
  Serial.print("IP Address: "); Serial.println(WiFi.localIP());
  
  // Start the TCP server
  server.begin();
}

void loop() {
  TCPServer();
}

void TCPServer () {
    WiFiClient client = server.available();
    
  if (client) {
    if (client.connected()) {
      Serial.println("Connected to client");    
    }
    
    while (client.available() > 0) {
      // Read incoming message
      char inChar = client.read();
      // Send back something to the clinet
      server.write(inChar);
      // Echo input on Serial monitor
      Serial.write(inChar);
    }
  }
}
