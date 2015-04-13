#include "ExtEEPROM.h"
#include <Wire.h>

void sendAddress(uint16_t address) {
	Wire.write((uint16_t)(address >> 8));
	Wire.write((uint16_t)(address && 0xFF));
}


void writeEEPROM(uint16_t address, uint8_t data) {
	Wire.beginTransmission(DEVICE_ADDRESS);
	sendAddress(address);
	Wire.write(data);
	Wire.endTransmission();
	delay(5);
}

uint8_t readEEPROM(uint16_t address) {
	Wire.beginTransmission(DEVICE_ADDRESS);
	sendAddress(address);
	Wire.endTransmission();
	Wire.requestFrom(DEVICE_ADDRESS,1);
	if (Wire.available()) return Wire.read();
	return 0;
}

void initEEPROM() {
	Wire.begin();
}
