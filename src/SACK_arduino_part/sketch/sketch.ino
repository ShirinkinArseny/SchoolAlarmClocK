#include <EEPROM.h>

#include <Time.h>
#include "RingKeeper.h"

#define USE_EXTERNAL_EEPROM

void setup() {
  setTime(8, 59, 50, 9, 2, 2015);
  initRingKeeper(11);
}

void loop() {
  updateRingKeeper();
}
