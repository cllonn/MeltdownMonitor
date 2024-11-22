#define USE_ARDUINO_INTERRUPTS true
 
#include <PulseSensorPlayground.h>  // Include the PulseSensor Playground library
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <ArduinoHttpClient.h>
#include <OneWire.h>
#include <DallasTemperature.h>

// WiFi credentials
const char* ssid = "AlJaziri1";
const char* password = "0506335446";

// Google Sheets Script Web App URL
const char* host = "script.google.com";
const int port = 443;
const char* googleScriptID = "AKfycbzjNgvS3AjvaOd3LrTiSKqP5kdbnP4ZfRHPXhyjSkxntIb1L3ncbTB9SskMJXg4aP34cw";

//pin initialization 
const int gsrPin = 35; 
const int PULSE_SENSOR_PIN = 32;  // PulseSensor connected to analog pin A1
#define ONE_WIRE_BUS 25


PulseSensorPlayground pulseSensor;
WiFiClientSecure wifiClient;
HttpClient httpClient = HttpClient(wifiClient, host, port);
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

float Celsius = 0;

void setup() {
  Serial.println("ENTRER SETUP LOOP");
  sensors.begin();
  Serial.begin(115200);

  // Connect to WiFi
  Serial.println("BEFORE CONNECTION");
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.println("WiFi connected.");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  // Disable SSL verification (not recommended for production)
  wifiClient.setInsecure();

  pulseSensor.analogInput(PULSE_SENSOR_PIN);  // Set the PulseSensor input pin
  pulseSensor.setThreshold(550);  // Optional: Adjust the threshold for detecting heartbeats

  // Start the PulseSensor with serial output
  if (pulseSensor.begin()) {
    Serial.println("PulseSensor initialized successfully.");
  } else {
    Serial.println("PulseSensor initialization failed. Check wiring.");
  }
}

void loop() {
  sensors.requestTemperatures();
  Celsius = sensors.getTempCByIndex(0);
  Serial.println(Celsius);
  
  int gsrValue = analogRead(gsrPin);
  Serial.println(gsrValue);

  int myBPM = pulseSensor.getBeatsPerMinute();  // Get the BPM value

  // Check if a beat was detected
  if (pulseSensor.sawStartOfBeat()) {
    Serial.print("BPM: ");
    Serial.println(myBPM);  // Print the BPM to the Serial Monitor
  }
  
  sendDataToGoogleSheets(myBPM, gsrValue, Celsius);
  delay(10);
}


void sendDataToGoogleSheets(int bpm, int gsr, float temp) {
  String url = "/macros/s/" + String(googleScriptID) + "/exec?";
  url += "value1=" + String(bpm) + "&value2=" + String(gsr) + "&value3=" + String(temp);

  Serial.print("Requesting URL: ");
  Serial.println(url);

  // Start HTTP GET request
  httpClient.get(url);

  // Read response
  int statusCode = httpClient.responseStatusCode();
  String response = httpClient.responseBody();

  Serial.print("Status code: ");
  Serial.println(statusCode);
  Serial.print("Response: ");
  Serial.println(response);

  httpClient.stop();
} 
