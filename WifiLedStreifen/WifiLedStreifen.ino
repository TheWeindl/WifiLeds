#include <ESP8266WiFi.h>

//const char* ssid = "PBS-370351";
//const char* password = "RVLO1QB9K8XWAw8Xk34S9Iv9";
const char* ssid = "NETGEAR24";
const char* password = "orangetrail517";
const char* host = "192.168.1.4";         //IP des Java-Servers
const int serverPort = 5045;              //Port des Java-Servers (ServerSocket)
const int id = 1;                         //Device ID for identification
const int interval = 1;                   //Update Requests per second


void setup() {
  Serial.begin(115200); //Kontrollausgabe aktivieren
  delay(800);

  Serial.println();
  Serial.print("Versuche Verbindung zum AP mit der SSID=");
  Serial.print(ssid);
  Serial.println(" herzustellen");
  
  WiFi.begin(ssid, password);

  /*Solange keine Verbindung zu einem AccessPoint (AP) aufgebaut wurde*/
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.print("Verbunden mit IP ");
  Serial.println(WiFi.localIP());
  /*Signalstärke des AP*/
  long rssi = WiFi.RSSI();
  Serial.print("Signalstaerke(RSSI) des AP:");
  Serial.print(rssi);
  Serial.println(" dBm");
}


void loop() {

  WiFiClient client;
  
  if (!client.connect(host, serverPort)) {
    Serial.print("X");
    return;
  }
  
  Serial.println();
  Serial.print("Connected to ");
  Serial.println(host);

  String request = "Request update ID";
  Serial.print("Sending: ");
  Serial.print(request); Serial.println(id);
  client.print(request); client.println(id);
  delay(200);
  
  String line = client.readStringUntil('\n');
  Serial.println(line);
  
  client.flush();
  client.stop();
  Serial.println("Connection closed"); 

  delay(1000/interval);
}   

