#include "LedControl.h"
LedControl lc=LedControl(12,11,10,4);
  int setPixel(int x, int y, bool state){
    int device;
    if(x<32){
      if(x<8) device= 3;
      else if(x<16) device= 2;
      else if(x<24) device= 1;
      else if(x<32) device= 0;
      lc.setLed(device,7-y,x%8,state);
    }
  }
void setup() {
  Serial.begin(9600);
  Serial.setTimeout(10);
  int devices=lc.getDeviceCount();
  for(int address=0;address<devices;address++) {
    lc.shutdown(address,false);
    lc.setIntensity(address,0);
    lc.clearDisplay(address);
  }
}

void loop() { 
  if (Serial.available() > 0) {
    int x = Serial.parseInt();
    int y = Serial.parseInt();
    int state = Serial.parseInt();
    setPixel(x,y,state);

  }
}
