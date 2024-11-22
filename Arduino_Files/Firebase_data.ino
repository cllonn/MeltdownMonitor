#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <PulseSensorPlayground.h>

// WiFi and Firebase Configuration
#define WIFI_SSID "Ali Saad"
#define WIFI_PASSWORD "0504010444"
#define FIREBASE_API_KEY "AIzaSyC4Ux1nbETM1TQM9H-DLW6aPzCcqmd5HVg"
#define DATABASE_URL "https://meltdownmonitor-e6d3c-default-rtdb.europe-west1.firebasedatabase.app/"

// Pin Definitions
const int GSR_PIN = 35;
const int PULSE_SENSOR_PIN = 32;
#define ONE_WIRE_BUS 25

// Firebase and Sensor Objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
PulseSensorPlayground pulseSensor;
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

unsigned long previousMillis = 0;
bool firebaseReady = false;
int hrData = 0, gsrData = 0;
float tempData = 0;

void setup() {
  Serial.begin(115200);
  connectToWiFi();
  initFirebase();
  initSensors();
}

void loop() {
  if (Firebase.ready() && firebaseReady && millis() - previousMillis > 5000) {
    previousMillis = millis();
    readAndSendData();
  }
}

void connectToWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println("\nConnected to Wi-Fi, IP: " + WiFi.localIP().toString());
}

void initFirebase() {
  config.api_key = FIREBASE_API_KEY;
  config.database_url = DATABASE_URL;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("Firebase connected");
    firebaseReady = true;
  } else {
    Serial.printf("Firebase connection failed: %s\n", config.signer.signupError.message.c_str());
  }
}

void initSensors() {
  sensors.begin();
  pulseSensor.analogInput(PULSE_SENSOR_PIN);
  pulseSensor.setThreshold(1);
  pulseSensor.begin();
}

void readAndSendData() {
  // Heart Rate (HR) data
  hrData = pulseSensor.getBeatsPerMinute();
  if (pulseSensor.sawStartOfBeat()) {
    Serial.println("BPM: " + String(hrData));
    sendData("/SensorData/HR", hrData);
  }

  // Temperature data
  sensors.requestTemperatures();  // Request temperature from the sensor
  tempData = sensors.getTempCByIndex(0);  // Get the temperature from the first sensor
  Serial.println("Temperature: " + String(tempData));
  sendData("/SensorData/Temperature", tempData);
  


  // GSR data
  gsrData = analogRead(GSR_PIN);
  Serial.println("GSR: " + String(gsrData));
  sendData("/SensorData/GSR", gsrData);
}

template <typename T>
void sendData(const String& path, T value) {
  if (Firebase.RTDB.set(&fbdo, path.c_str(), value)) {
    Serial.println("Data sent to " + path);
  } else {
    Serial.println("Failed to send data to " + path + ": " + fbdo.errorReason());
  }
}
