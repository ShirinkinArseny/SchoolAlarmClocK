#include <EEPROM.h>

#include "Ring.h"

#define RINGS_PER_DAY 71 //Максимально допустимое число звонков в день
#define RINGS_DATABLOCK_START 511 //Сдвиг блока таймстампов от начала EEPROMа

#define SHORT_RING_MASK 0b1000000000000000 //Маска длительности звонка
#define TIMESTAMP_RING_MASK 0b0011111111111111 //Маска времени звонка
#define EMPTY_RING_MASK 0b0100000000000000 //Маска пустого звонка


//#define USE_EXTERNAL_EEPROM
#ifdef USE_EXTERNAL_EEPROM
#include "ExtEEPROM.h"
#define readByte(address) readEEPROM(address)
#define writeByte(address,value) writeEEPROM(address, value)
#else //In case of internal
#define readByte(address) EEPROM.read(address)
#define writeByte(address, value) EEPROM.write(address, value)
#endif  //USE_EXTERNAL_EEPROM

/*
Создаём звонок из двух хранимых байт (из представления в памяти)
*/
Ring::Ring (uint16_t memoryRepresentation) {
  Ring::memoryRepresentation=memoryRepresentation;
}

/*
Создаём звонок из его времени
*/
Ring::Ring (byte hours, byte minutes, byte seconds) {
  Ring::memoryRepresentation=
      (3600*hours+
      60*minutes+
      seconds)/10;
}

/*
Получаем звонок по его номеру
*/
Ring loadByAddress (byte address) {
  byte doubled=address*2;
  return Ring(readByte(RINGS_DATABLOCK_START+doubled)*256+readByte(RINGS_DATABLOCK_START+doubled+1));
}

/*
Получаем представление в памяти
*/
uint16_t Ring::getMemoryRepresentation() {
  return  Ring::memoryRepresentation;
}

long Ring::getSecondFromDayStart() {
  return (Ring::memoryRepresentation & TIMESTAMP_RING_MASK)*10L;
}

/*
Загружаем все звонки за день недели (пн - 0, вт - 1 и т.д.)
После использования нужно удалять!
*/
Ring* getDayRings(byte dayOfWeek) {
  int index=RINGS_PER_DAY*dayOfWeek;

  byte ringsNumber=readByte(index);

  Ring *rings=(Ring *) malloc(sizeof(Ring) * (ringsNumber+1));

  for (byte i=0; i<ringsNumber; i++) {
    rings[i]=loadByAddress(readByte(index+i+1));
  }
  rings[ringsNumber]=Ring(EMPTY_RING_MASK);

  return rings;
}

/*
Загружаем ссылки дня на таймстампы звонков.
После использования нужно удалять!
*/
byte* getDayMemoryRepresentation(byte dayOfWeek) {

  int index=RINGS_PER_DAY*dayOfWeek;

  byte ringsNumber=readByte(index);

  byte *bytes=(byte *) malloc(sizeof(byte) * (ringsNumber+1));

  for (byte i=0; i<ringsNumber; i++) {
    bytes[i+1]=readByte(index+i+1);
  }
  bytes[0]=ringsNumber;

  return bytes;
}

/*
Таймстамп по его номеру и смещению
*/
byte getRingTimeStamp(byte index, byte bytenum) {
    return readByte(RINGS_DATABLOCK_START+index*2+bytenum);
}

/*
Узнаём, действительно ли звонок существует (или это просто маркер конца массива)
*/
boolean Ring::isEmpty() {
  return (Ring::memoryRepresentation & EMPTY_RING_MASK) != 0;
}

/*
Узнаём, короткий ли звонок
*/
boolean Ring::isShort() {
  return (Ring::memoryRepresentation & SHORT_RING_MASK) != 0;
}

/*
Сохраняем звонок в пзу по заданному индексу звонка
*/
void Ring::writeToEEPROM(byte index) {
  int doubled=index*2;
  writeByte(RINGS_DATABLOCK_START+doubled, Ring::memoryRepresentation/256);
  writeByte(RINGS_DATABLOCK_START+doubled+1, Ring::memoryRepresentation%256);
}

/*
Пишем в ПЗУ ссылку на звонок в дне
*/
void writeDayRingToEEPROM(byte day, byte dayRingNumber, byte ringNumber) {
    writeByte(day*RINGS_PER_DAY+dayRingNumber+1, ringNumber);
}

/*
Пишем в ПЗУ количество звонков в дне
*/
void writeDayRingsNumberToEEPROM(byte day, byte ringsNumber) {
    writeByte(day*RINGS_PER_DAY, ringsNumber);
}

/*
Пишем в ПЗУ количество таймстампов звонков
*/
void writeTimeStampsCountToEEPROM(byte tsCount) {
    writeByte(RINGS_DATABLOCK_START-1, tsCount);
}

/*
Количество таймстампов звонков
*/
byte getRingsTimeStampsNumber() {
    return readByte(RINGS_DATABLOCK_START-1);
}

/*
Пишем в ПЗУ стандартное расписание и стандартные звонки
*/
void writeDefaultRings() {
  for (byte day=0; day<7; day++) {
    writeDayRingsNumberToEEPROM(day, 0);
  }
  writeByte(RINGS_DATABLOCK_START-1, 0);
}


