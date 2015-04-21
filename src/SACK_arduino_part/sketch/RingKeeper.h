#ifndef RingKeeper_header_check
#define RingKeeper_header_check

#include <Arduino.h>

/*
Инициализация хранителя звонков
*/
void initRingKeeper(byte ringPin);

/*
Обновление хранителя звонков
*/
void updateRingKeeper();

#endif