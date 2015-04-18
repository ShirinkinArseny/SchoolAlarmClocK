#ifndef _EXT_EEPROM_H
#define _EXT_EEPROM_H

#include <Arduino.h>

#ifndef EEPROM_ADDRESS
#define EEPROM_ADDRESS B1010110 //EEPROM slave address
#endif

//Инициализация EEPROM
void initEEPROM();

//Запись в EEPROM по адресу
void writeEEPROM(uint16_t address, uint8_t data);

//Чтение из EEPROM по адресу
uint8_t readEEPROM(uint16_t address);
#endif
