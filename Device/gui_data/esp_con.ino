#include "serial_v1.h"
Serial_make myserial;
void setup() {
  delay(3000);
  Serial.begin(115200);
  
  myserial.open_send(12, 14, 5);// D6 D5
  myserial.write("123");
}

void loop() {
  myserial.write("123");
  delay(1000);
}
