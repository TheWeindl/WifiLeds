#include <ESP8266WiFi.h>
#include <ArduinoJson.h>

// KRM
const char* ssid = "PBS-370351";
const char* password = "RVLO1QB9K8XWAw8Xk34S9Iv9";
const char* host = "10.0.0.13";

// HGB 
//const char* ssid = "NETGEAR24";
//const char* password = "orangetrail517";
//const char* host = "192.168.1.4";         //IP des Java-Servers

const int serverPort = 5045;                //Port des Java-Servers (ServerSocket)
const int interval = 1;                     //Update Requests per second

// JSON STRUCTURE
const int mId = 1;                          
int mStatus = 0;
int mRed = 0;
int mGreen = 0;
int mBlue = 0;

// LED PIN DEFINITIONS
int redPin = 16;    // D0
int greenPin = 5;   // D1
int bluePin = 4;    // D2

void setup() {
  Serial.begin(115200);
  delay(800);  
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

void loop()
{
  WiFiClient client; 
  OutputColor(); 

  //Connect to the server
  if (!client.connect(host, serverPort)) 
  {
    Serial.print("X");
    return;
  }

  if(client.connected())
  {
    String res = "";
    
    RequestUpdate(client);
    delay(200);
    res = GetResponse(client);
    ProcessResponse(res);
    ShutdownConnection(client);    
  }    
  
  delay(2000);
}

void OutputColor()
{
  if(mStatus)
  {
    analogWrite(redPin, mRed);   
    analogWrite(greenPin, mGreen);      
    analogWrite(bluePin, mBlue);
  }
  else
  {
    const int off = 0;
    analogWrite(redPin, off);   
    analogWrite(greenPin, off);      
    analogWrite(bluePin, off);
  }  
}

void RequestUpdate(WiFiClient & client)
{
  //Build the request in json structure and send it to the client
  char request[150];
  sprintf(request,"{\"id\":%d,\"status\":%d,\"change\":%d,\"color\":[%d,%d,%d]}",mId,mStatus,0,mRed,mGreen,mBlue);
  client.println(request);
}

void ShutdownConnection(WiFiClient & client)
{
  //Send the disconnect command and shutdown the socket
  client.println("END");
  client.flush();
  client.stop();

  //Wait until the server is finally disconnected
  while(client.connected())
  {
    Serial.print(".");
    delay(100);
  }
  Serial.println("Connection closed");
}

String GetResponse(WiFiClient & client)
{
  String response = "ERROR";
  while(client.connected())
  {
    if(client.available())
    {
      response = client.readStringUntil('\n');
      break;
    }    
  }  
  return response;
}

void ProcessResponse(String const & str)
{
  StaticJsonBuffer<200> inputBuffer;
  JsonObject& msg = inputBuffer.parseObject(str);

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
}

