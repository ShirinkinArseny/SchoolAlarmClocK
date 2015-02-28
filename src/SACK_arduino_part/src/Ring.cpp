#include <EEPROM.h>

#include "Ring.h"

#define RINGS_PER_DAY 71
#define LENGTH_MULTIPLYER 0b100000000000000
#define EMPTY_RING 0b1111111111111111

#define RINGS_DATABLOCK_START 512

#ifdef USE_EXTERNAL_EEPROM
#include "ExtEEPROM.h"
#define readByte(address) readEEPROM(address)
#define writeByte(address,value) writeEEPROM(address,value)
#else //In case of internal
#define readByte(address) EEPROM.read(address)
#define writeByte(address, value) EEPROM.write(address.value)
#endif  //USE_EXTERNAL_EEPROM


Ring::Ring (uint16_t memoryRepresentation) {
  Ring::memoryRepresentation=memoryRepresentation;
}

Ring::Ring (byte hours, byte minutes, byte seconds) {
  Ring::memoryRepresentation=
      (3600*hours+
      60*minutes+
      seconds)/10;
}

Ring loadByAddress (byte address) {
  byte doubled=address*2;
  return Ring(readByte(RINGS_DATABLOCK_START+doubled)*256+readByte(RINGS_DATABLOCK_START+doubled+1));
}


uint16_t Ring::getMemoryRepresentation() {
  return  Ring::memoryRepresentation;
}

/*byte Ring::getTimeLength() {
  return bitRead(Ring::memoryRepresentation, 0)*2+bitRead(Ring::memoryRepresentation, 1)+1;
}*/

long Ring::getSecondFromDayStart() {
  return Ring::memoryRepresentation*10L;
}

Ring* getDayRings(byte dayOfWeek) {
  int index=RINGS_PER_DAY*dayOfWeek;

  byte ringsNumber=readByte(index);

  Ring *rings=(Ring *) malloc(sizeof(Ring) * (ringsNumber+1));

  for (byte i=0; i<ringsNumber; i++) {
    rings[i]=loadByAddress(readByte(index+i+1));
  }
  rings[ringsNumber]=Ring(EMPTY_RING);

  return rings;
}

boolean Ring::isEmpty() {
  return Ring::memoryRepresentation==EMPTY_RING;
}

void Ring::writeToEEPROM(byte index) {
  byte doubled=index*2;
  writeByte(RINGS_DATABLOCK_START+doubled, Ring::memoryRepresentation/256);
  writeByte(RINGS_DATABLOCK_START+doubled+1, Ring::memoryRepresentation%256);
}


void writeDayRingToEEPROM(byte day, byte dayRingNumber, byte ringNumber) {
    writeByte(day*71+dayRingNumber+1, ringNumber);
}

void writeDayRingsNumberToEEPROM(byte day, byte ringsNumber) {
  writeByte(day*71, ringsNumber);
}

void writeDefaultRings() {


  for (byte day=0; day<7; day++) {
    writeDayRingsNumberToEEPROM(day, 0);
  }

}


