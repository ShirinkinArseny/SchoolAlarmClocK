#include <EEPROM.h>

#include <Time.h>
#include "RingKeeper.h"
#include <Wire.h>

void setup() {
  setTime(0, 0, 0, 9, 1, 2015);
  initRingKeeper(11);
}

void loop() {
  updateRingKeeper();
}
