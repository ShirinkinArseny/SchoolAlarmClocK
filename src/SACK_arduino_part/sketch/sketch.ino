#include <EEPROM.h>

#include <Time.h>
#include "RingKeeper.h"
#include <Wire.h>

void setup() {
  setTime(8, 59, 50, 9, 2, 2015);
  initRingKeeper(11);
  Wire.begin();
}

void loop() {
  updateRingKeeper();
}
