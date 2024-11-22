#define USE_ARDUINO_INTERRUPTS true
#include <PulseSensorPlayground.h>
#include <OneWire.h>
#include <DallasTemperature.h>

const int gsrPin = 35;
const int PULSE_SENSOR_PIN = 32;  // PulseSensor connected to analog pin A1
#define ONE_WIRE_BUS 25

PulseSensorPlayground pulseSensor;
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

float Celsius = 0;

void setup() {
  sensors.begin();
  Serial.begin(9600);

  // Initialize Pulse Sensor
  pulseSensor.analogInput(PULSE_SENSOR_PIN);  // Set the PulseSensor input pin
  pulseSensor.setThreshold(500);  // Optional: Adjust the threshold for detecting heartbeats

  if (pulseSensor.begin()) {
    //Serial.println("PulseSensor initialized successfully.");
  } else {
    //Serial.println("PulseSensor initialization failed. Check wiring.");
  }
}

void loop() {
  // Calculate elapsed time using millis()
  unsigned long elapsedMillis = millis();
  unsigned long seconds = (elapsedMillis / 1000) % 60;
  unsigned long minutes = (elapsedMillis / (1000 * 60)) % 60;
  unsigned long hours = (elapsedMillis / (1000 * 60 * 60)) % 24;
  unsigned long tenths = (elapsedMillis % 1000) / 100; // Get tenths of a second

  // Print stopwatch time (HH:MM:SS.t)
  Serial.printf("%02lu:%02lu:%02lu.%01lu,", hours, minutes, seconds, tenths);

  // Request temperature and print sensor data
  sensors.requestTemperatures();
  Celsius = sensors.getTempCByIndex(0);
  Serial.print(Celsius);
  Serial.print(",");

  int gsrValue = analogRead(gsrPin);
  Serial.print(gsrValue);
  Serial.print(",");

  int myBPM = pulseSensor.getBeatsPerMinute();
  Serial.print(myBPM);
  Serial.println();

  delay(100);  // Delay to control data output frequency
}
