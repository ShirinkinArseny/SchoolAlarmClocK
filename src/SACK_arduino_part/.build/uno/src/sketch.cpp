#include <Arduino.h>
#include <EEPROM.h>
#include "Time.h"
#include "RingKeeper.h"
void setup();
void loop();
#line 1 "src/sketch.ino"
//#include <EEPROM.h>

//#include "Time.h"
//#include "RingKeeper.h"

void setup() {
  setTime(8, 59, 50, 9, 2, 2015);
  initRingKeeper(11);
}

void loop() {
  updateRingKeeper();
}