#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

const char* ssid = "PBS-370351";
const char* password = "RVLO1QB9K8XWAw8Xk34S9Iv9";
//const char* ssid = "NETGEAR24";
//const char* password = "orangetrail517";
//const char* host = "192.168.1.4";         //IP des Java-Servers
const char* host = "10.0.0.13";
const int serverPort = 5045;              //Port des Java-Servers (ServerSocket)
const int interval = 1;                   //Update Requests per second

int mStatus = 0;
const int mId = 1;                         //Device ID for identification on the server
int mRed = 0;
int mGreen = 0;
int mBlue = 0;

int redPin = 16;    // D0
int greenPin = 5;   // D1
int bluePin = 4;    // D2

void setup() {
  Serial.begin(115200);
  delay(800);  

  //Configure LED Ports
  

  Serial.println();
  Serial.print("Versuche Verbindung zum AP mit der SSID=");
  Serial.print(ssid);
  Serial.println(" herzustellen");
  
  WiFi.begin(ssid, password);

  //Wait for connection
  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(800);
  }

  Serial.print("Verbunden mit IP ");
  Serial.println(WiFi.localIP());
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength:");
  Serial.print(rssi);
  Serial.println(" dBm");
}

void loop() {

  WiFiClient client;

  //Change LED Pins according to the intern color values
  OutputColor();
  
  if (!client.connect(host, serverPort)) {
    Serial.print("X");
    return;
  }

  StaticJsonBuffer<200> jsonBuffer;
  StaticJsonBuffer<200> inputBuffer;
  JsonObject& requestJson = jsonBuffer.createObject();
  JsonArray& colorArr = requestJson.createNestedArray("color");
  
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


  String json = "no input";
  if(client.available())
  {
    //Get answer form server
    json = client.readStringUntil('\n');
    Serial.print("Data: ");
    Serial.println(json);
  }
  else
  {
    Serial.println("Server not available");
  }
  Serial.print("Data: ");
  Serial.println(json);
 
  JsonObject& msg = inputBuffer.parseObject(json);
  client.flush();
  client.stop();

  if(!msg.success())
  {
    Serial.println("parseObject() failed");
    return;
  }

  mStatus  = msg["status"]; 
  mRed      = msg["color"][0];
  mGreen    = msg["color"][1];
  mBlue     = msg["color"][2];  
  
  Serial.print(mRed); Serial.print(" ");
  Serial.print(mGreen); Serial.print(" ");
  Serial.println(mBlue);
  
  delay(1000/interval);
}   

void OutputColor()
{
  analogWrite(redPin, mRed);   
  analogWrite(greenPin, mGreen);      
  analogWrite(bluePin, mBlue);
}

