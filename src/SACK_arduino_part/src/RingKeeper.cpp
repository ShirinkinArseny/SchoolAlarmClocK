#include <Arduino.h>

#include "J35DL.h"
#include "Ring.h"
#include "Time.h"

long getCurrentTime() {
    return 3600L*hour()+60L*minute()+second();
}

#define DELAY_TIME_COEF 2000   //длительность стандартного звонка

Ring* weekRings; //все звонки сегодня.

byte currentWeekDay=0; //текущий день недели

byte ringVoltagePin; //пин, на который подаётся напряжение при звонке

//получаем текущий день недели
byte getWeekDay() {
  byte wDay=weekday()-2;
  if (wDay<0) wDay+=7;
  return wDay;
}
long lastTimeSec=0;     //прошлое время от начала дня в секундах
long currentTimeSec=0;  //текущее время от начала дня в секундах

void loadTodayRings() {
             delete(weekRings);
             weekRings=getDayRings(currentWeekDay);
}

void initRingKeeper(byte ringPin) {
  Serial.begin(9600);
  ringVoltagePin=ringPin;
  pinMode(ringVoltagePin, OUTPUT);
  initConnection(13, 12);

  //writeDefaultRings();

  currentWeekDay=getWeekDay();
  weekRings=getDayRings(currentWeekDay);

  lastTimeSec=getCurrentTime();
}

int getFreeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void printFreeMemory() {
Serial.print("FREEMEM: ");
Serial.print(getFreeRam());
Serial.print("\n");
}

void makeRing() {

  digitalWrite(ringVoltagePin, HIGH);
  delay(DELAY_TIME_COEF);
  digitalWrite(ringVoltagePin, LOW);

}

char* done="Done!";

int readValue() {
    int value=Serial.read();

    byte cyc=0;

    while (value<0 && cyc<10) {
            value=Serial.read();
            cyc++;
            delay(10);
    }

    return value;
}

boolean checkForWrongRead(int value, char* error, int day, int ring) {
    if (value<0) {
             Serial.print(error);
             Serial.print(':');
             Serial.print(day);
             Serial.print(':');
             Serial.println(ring);
             loadTodayRings();
             return true;
    }
    return false;
}

void readNewRingsTableFromSerial() {
/*

            Получаем таблицу звонков в виде

                Количество звонков в первый день
                Первый звонок (индекс) первого дня
                ..
                Последний звонок (индекс) первого дня
                Количество звонков во второй день
                Первый звонок (индекс) второго дня
                ..
                Последний звонок (индекс) второго дня
                ..
                Последний звонок (индекс) последнего дня
                Количество звонков
                Первый звонок
                Второй звонок
                ...
                Последний звонок

            Преобразуем звонки в их представление в ПЗУ

        */

            /*
                Читаем ссылки на звонки
            */
            for (byte day=0; day<7; day++) {

                int size=readValue(); //Количество звонков в day-ый день
                if (checkForWrongRead(size, "E1", day, -1)) return;

                writeDayRingsNumberToEEPROM(day, size);

                for (byte ring=0; ring<size; ring++) {

                    int link=readValue();
                    if (checkForWrongRead(link, "E2", day, ring)) return;
                    writeDayRingToEEPROM(day, ring, link);
                }
            }

            int size=readValue(); //Количество звонков
            if (checkForWrongRead(size, "E3", 7, 0)) return;

            /*
                Читаем звонки
            */
            for (byte ring=0; ring<size; ring++) {

                int b1=readValue();
                if (checkForWrongRead(b1, "E4", 7, ring)) return;

                int b2=readValue();
                if (checkForWrongRead(b2, "E5", 7, ring)) return;

                Ring(256*b1+b2).writeToEEPROM(ring);
            }
            loadTodayRings();
            Serial.print('R');
            Serial.println(done);
}

void printRingsTableToSerial() {
    Serial.print('[');
    for (byte day=0; day<7; day++) {

        delete(weekRings);
        weekRings=getDayRings(day);

        Serial.print('[');
        int ring=0;
        while (!weekRings[ring].isEmpty()) {
            Serial.print(weekRings[ring].getMemoryRepresentation());
            Serial.print(0);
            if (!weekRings[ring+1].isEmpty()) Serial.print(',');
            ring++;
        }
        Serial.print(']');
        if (day!=6) Serial.print(',');


    }
    Serial.println(']');
    loadTodayRings();
}

void readNewTimeFromSerial() {
    delay(20);


    byte hours=Serial.read();
    byte minutes=Serial.read();
    byte seconds=Serial.read();
    byte day=Serial.read();
    byte month=Serial.read();
    byte year1=Serial.read();
    byte year2=Serial.read();

    //hh-mm-ss-dd-mm-yyyy
    setTime(
    hours,
    minutes,
    seconds,
    day,
    month,
    year1*256+year2);

    Serial.print('T');
    Serial.println(done);

    currentTimeSec=getCurrentTime();
    lastTimeSec=currentTimeSec-1;
    currentWeekDay=getWeekDay();
}

void updateSerial() {

    int readed=Serial.read();

    if (readed!=-1) {

        switch (readed) {

            case '1':                       //get rings table
                printRingsTableToSerial();
                break;
            case '2':                       //get current time
                Serial.println(currentTimeSec);
                break;
            case '3':                       //set rings table
                readNewRingsTableFromSerial();
                break;
            case '4':                       //get free memory
                printFreeMemory();
                break;
            case '5':                       //get week day
                Serial.println(getWeekDay());
                break;
            case '6':                       //set time
                readNewTimeFromSerial();
                break;

        }
    }
}

void updateRingKeeper() {

    updateSerial();

    lastTimeSec=currentTimeSec;
    currentTimeSec=getCurrentTime();
    
    if (currentTimeSec<lastTimeSec) {
          //полночь, переход между днями
        lastTimeSec=0;
        currentWeekDay=getWeekDay();
        loadTodayRings();
      return;
    }

    byte i=0;
    while (!weekRings[i].isEmpty()) {
      long ringTime=weekRings[i].getSecondFromDayStart();
      if (currentTimeSec>=ringTime && lastTimeSec<ringTime) {

	    makeRing();

	    break;
      }
      i++;
    }
}