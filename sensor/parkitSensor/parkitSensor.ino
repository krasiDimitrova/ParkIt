// Install from zip - https://playground.arduino.cc/Code/NewPing/
#include <NewPing.h>

// Install from Sketch -> Include Library -> Manage Libraries
#include <ArduinoJson.h>
#include <WiFiMulti.h>

#include <HTTPClient.h>
#include <WiFi.h>

#define FIRST_PARKING_SPACE_UUID "aa382142-8f02-4d74-9006-d0cf188e3a21"
#define FIRST_TRIGGER_PIN 5
#define FIRST_ECHO_PIN 18

#define SECOND_PARKING_SPACE_UUID "44ef65d2-3be6-4905-b90b-bce8c97f7339"
#define SECOND_TRIGGER_PIN 22
#define SECOND_ECHO_PIN 23

#define MAX_DISTANCE 20  // Distane between car and sensor in cm (min 2-3cm for the sensor to work)

#define SERVER_URL "http://192.168.0.169:8080/api/v1/parking-space/status"

// https://core-electronics.com.au/tutorials/how-to-use-ultrasonic-sensors.html
NewPing firstParkingSpace(FIRST_TRIGGER_PIN, FIRST_ECHO_PIN, MAX_DISTANCE);
NewPing secondParkingSpace(SECOND_TRIGGER_PIN, SECOND_ECHO_PIN, MAX_DISTANCE);

bool isFirstFree = true;
bool isSecondFree = true;

WiFiMulti wiFiMulti;
HTTPClient http;

void setup() {
  Serial.begin(115200L);
  connectToWIFI();
}


void loop() {
  // First parking space just taken
  Serial.println("First distance");
  Serial.println(firstParkingSpace.ping_cm());

  if (firstParkingSpace.ping_cm() != 0 && isFirstFree) {
    Serial.println("taken");
    isFirstFree = false;
    updateFirstParkingSpaceStatus();
  }

  // First parkign space just freed
  if (firstParkingSpace.ping_cm() == 0 && !isFirstFree) {
    isFirstFree = true;
    updateFirstParkingSpaceStatus();
  }

  Serial.println("Second distance");
  Serial.println(secondParkingSpace.ping_cm());

  // Second Parking space just taken
  if (secondParkingSpace.ping_cm() != 0 && isSecondFree) {
    isSecondFree = false;
    updateSecondParkingSpaceStatus();
  }

  // Second Parkign space just freed
  if (secondParkingSpace.ping_cm() == 0 && !isSecondFree) {
    isSecondFree = true;
    updateSecondParkingSpaceStatus();
  }

  delay(1500);
}

// https://www.techcoil.com/blog/how-to-post-json-data-to-a-http-server-endpoint-from-your-esp32-development-board-with-arduinojson/
void updateFirstParkingSpaceStatus() {
  connectToWIFI();

  String requestBody = createRequestBody(String(FIRST_PARKING_SPACE_UUID), isFirstFree ? "true" : "false");
  sendRequestToServer(SERVER_URL, requestBody);
}

void updateSecondParkingSpaceStatus() {
  connectToWIFI();

  String requestBody = createRequestBody(String(SECOND_PARKING_SPACE_UUID), String(isSecondFree));
  sendRequestToServer(SERVER_URL, requestBody);
}

String createRequestBody(String sensorIdentifier, String isFree) {
  return "{\"sensorIdentifier\": \"" + sensorIdentifier + "\", \"isFree\":" + isFree + "}";
}

void sendRequestToServer(String serverUrl, String requestBody) {
  http.begin(serverUrl.c_str());
  http.addHeader("Content-Type", "application/json");
  http.addHeader("PARKIT_PARKING_SPACE_SHARED_SECRET", "f4255256-94a4-473b-b8c9-26b7b7da91d8");

  int httpCode = http.POST(requestBody);  //Send the request
  String payload = http.getString();      //Get the response payload

  Serial.println(httpCode);  //Print HTTP return code
  Serial.println(payload);   //Print request response payload

  http.end();
}

void connectToWIFI() {
  if (wiFiMulti.run() == WL_CONNECTED) {
    return;
  }

  const char* ssid = "";
  const char* password = "";

  wiFiMulti.addAP(ssid, password);

  Serial.println("Waiting for WiFi... ");

  while (wiFiMulti.run() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
}
