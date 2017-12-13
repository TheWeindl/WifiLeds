#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

//const char* ssid = "PBS-370351";
//const char* password = "RVLO1QB9K8XWAw8Xk34S9Iv9";
const char* ssid = "NETGEAR24";
const char* password = "orangetrail517";
const char* host = "192.168.1.4";         //IP des Java-Servers
const int serverPort = 5045;              //Port des Java-Servers (ServerSocket)
const int interval = 1;                   //Update Requests per second

bool mStatus = false;
const int mId = 1;                         //Device ID for identification
int mRed = 0;
int mGreen = 0;
int mBlue = 0;

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

  DynamicJsonBuffer jsonBuffer;
  WiFiClient client;
  
  if (!client.connect(host, serverPort)) {
    Serial.print("X");
    return;
  }

  JsonObject& requestJson = jsonBuffer.createObject();
  JsonArray& colorArr = requestJson.createNestedArray("color");
  
  //Serial.println();
  //Serial.print("Connected to ");
  //Serial.println(host);

  String request = "Request update ID ";
  //Serial.print("Sending: ");
  //Serial.print(request); Serial.println(mId);
  //client.print(request); client.println(id);

  //Build request JSON
  requestJson["id"] = mId;
  requestJson["status"] = mStatus;
  requestJson["change"] = 0;
  colorArr.add(mRed);
  colorArr.add(mGreen);
  colorArr.add(mBlue);  

  //Send JSON to server
  requestJson.printTo(client);
  
  delay(200);
  
  //Get answer form server
  String json = client.readStringUntil('\n');

  DynamicJsonBuffer inputBuffer;
  JsonObject& msg = inputBuffer.parseObject(json);

  if(!msg.success())
  {
    Serial.println("parseObject() failed");
    return;
  }

  mStatus  = msg["status"]; 
  mRed      = msg["color"][0];
  mGreen    = msg["color"][1];
  mBlue     = msg["color"][2];  
  
  client.flush();
  client.stop();
  //Serial.println("Connection closed"); 

  Serial.print(mRed); Serial.print(" ");
  Serial.print(mGreen); Serial.print(" ");
  Serial.println(mBlue);
  
  delay(1000/interval);
}   

