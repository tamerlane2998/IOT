
#include "serial_v1.h"
Serial_make myserial;

void resetkey()
{

  myserial.change();
}
void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  
  myserial.open_receive(2, 3);
  attachInterrupt(myserial.get_fre(), resetkey, CHANGE);

}

void loop() {
   
  if (myserial.available() > 0)
  {
   char p = myserial.read();
   Serial.print(p);
  }

}
