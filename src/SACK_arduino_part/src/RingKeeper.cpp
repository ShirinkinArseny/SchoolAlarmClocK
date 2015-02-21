#include <Arduino.h>

#include "J35DL.h"
#include "Ring.h"
#include "Time.h"

long getCurrentTime() {
    return 3600L*hour()+60L*minute()+second();
}

#define DELAY_TIME_COEF 10000   //длительность стандартного звонка

Ring* weekRings[7]; //все звонки на этой неделе. //TODO: LINKS!

byte currentWeekDay=0; //текущий день недели

byte ringVoltagePin; //пин, на который подаётся напряжение при звонке

//получаем текущий день недели
byte getWeekDay() {
  byte wDay=weekday()-2;
  if (wDay<0) wDay+=7;
  return wDay;
}

void loadWeekRings() {
  for (byte i=0; i<7; i++)
    weekRings[i]=getDayRings(i);
}

void clearWeekRings() {
    for (byte i=0; i<7; i++)
         delete(weekRings[i]);
}

long lastTimeSec=0;     //прошлое время от начала дня в секундах
long currentTimeSec=0;  //текущее время от начала дня в секундах

void initRingKeeper(byte ringPin) {
  Serial.begin(115200);
  ringVoltagePin=ringPin;
  pinMode(ringVoltagePin, OUTPUT);
  initConnection(13, 12);

  //writeDefaultRings();

  currentWeekDay=getWeekDay();
  loadWeekRings();

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

void updateSerial() {

    int readed=Serial.read();

    if (readed!=-1) {

    if (readed=='1') { //get rings table
        Serial.print('[');
        for (byte day=0; day<7; day++) {
            Serial.print('[');
            int ring=0;
            while (!weekRings[day][ring].isEmpty()) {
                Serial.print(weekRings[day][ring].getMemoryRepresentation());
                Serial.print(0);
                if (!weekRings[day][ring+1].isEmpty()) Serial.print(',');
                ring++;
            }
            Serial.print(']');
            if (day!=6) Serial.print(',');
        }
        Serial.println(']');
    } else
    if (readed=='2') { //get current time
        Serial.println(currentTimeSec);
    }
    if (readed=='3') { //set rings table

        /*

            Получаем таблицу звонков в виде

                Количество звонков в первый день
                Первый звонок первого дня
                ..
                Последний звоноко первого дня
                Количество звонков во второй день
                Первый звонок второго дня
                ..
                Последний звоноко второго дня
                ..
                Последний звоноко последнего дня

            Преобразуем звонки в их представление в ПЗУ

        */

            clearWeekRings();

            byte dayRingsNumbers[7];    //Число ссылок на звонки в каждом дне недели
            byte newDayRings[7][70];    //Ссылки на звонки в каждом дне недели
            uint16_t newRings[256]; //Сами звонки
            byte newRingsNumber=0;      //Количество звонков

            for (byte day=0; day<7; day++) {

                int size=Serial.read();
                 Serial.print(" got ");
                 Serial.print(size);
                    if (size<0) {
                             Serial.println("E0");
                             loadWeekRings();
                             return;
                    }

                dayRingsNumbers[day]=size;
                for (byte ring=0; ring<dayRingsNumbers[day]; ring++) {


                    int b1=Serial.read();
                 Serial.print(" got ");
                 Serial.print(b1);

                    if (b1<0) {
                             Serial.print("E1:");
                             Serial.print(ring);
                             Serial.print(":");
                             Serial.println(day);
                             loadWeekRings();
                             return;
                    }
                    int b2=Serial.read();
                 Serial.print(" got ");
                 Serial.print(b2);
                    if (b2<0) {
                             Serial.print("E2:");
                             Serial.print(ring);
                             Serial.print(":");
                             Serial.println(day);
                             loadWeekRings();
                             return;
                    }

                    uint16_t newRing=256*b1+b2;

                    boolean found=false;

                    for (byte i=0; i<newRingsNumber; i++) {
                        if (newRings[i]==newRing) {
                            found=true;
                            newDayRings[day][ring]=i;
                            break;
                        }
                    }

                    if (!found) {
                        if (newRingsNumber>=256) {
                             Serial.println("E3");
                             return;
                        }
                        newRings[newRingsNumber]=newRing;
                        newDayRings[day][ring]=newRingsNumber;
                        newRingsNumber++;
                    }
                }
            }

            for (byte day=0; day<7; day++) {
                writeDayRingsNumberToEEPROM(day, dayRingsNumbers[day]);
                for (byte ring=0; ring<dayRingsNumbers[day]; ring++) {
                    writeDayRingToEEPROM(day, ring, newDayRings[day][ring]);
                }
            }

            for (byte ring=0; ring<newRingsNumber; ring++) {
                Ring r=Ring(newRings[ring]);
                r.writeToEEPROM(ring);
            }

            loadWeekRings();
            Serial.println(done);
    } else
    if (readed=='4') { //get free memory
            printFreeMemory();
    } else
    if (readed=='5') { //get week day
            Serial.println(getWeekDay());
    } else
    if (readed=='6') { //set time

            delay(20);


            byte hours=Serial.read();
            byte minutes=Serial.read();
            byte seconds=Serial.read();
            byte day=Serial.read();
            byte month=Serial.read();
            byte year1=Serial.read();
            byte year2=Serial.read();

            Serial.print(hours); Serial.print(' ');
            Serial.print(minutes); Serial.print(' ');
            Serial.print(seconds); Serial.print(' ');
            Serial.print(day); Serial.print(' ');
            Serial.print(month); Serial.print(' ');
            Serial.print(year1*256+year2); Serial.print(' ');

            //hh-mm-ss-dd-mm-yyyy
            setTime(
            hours,
            minutes,
            seconds,
            day,
            month,
            year1*256+year2);

            Serial.println(done);

            currentTimeSec=getCurrentTime();
            lastTimeSec=currentTimeSec-1;
            currentWeekDay=getWeekDay();
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
      return;
    }

    byte i=0;
    while (!weekRings[currentWeekDay][i].isEmpty()) {
      long ringTime=weekRings[currentWeekDay][i].getSecondFromDayStart();
      if (currentTimeSec>=ringTime && lastTimeSec<ringTime) {

        startSending();


            sendText('\n');
            byte j=0;
            while (!weekRings[currentWeekDay][j].isEmpty()) {
                    sendNumber(weekRings[currentWeekDay][j].getSecondFromDayStart());
                    sendText(' ');
                    j++;
            }
            sendText('-');

            sendNumber(currentWeekDay);
            sendText(' ');
            sendNumber(currentTimeSec);
            sendText(' ');
            sendNumber(lastTimeSec);
            sendText(':');
            sendNumber(ringTime);
            sendText(' ');
            sendNumber(i);
            sendText('\n');
        stopSending();

	    makeRing();

        startSending();

            sendText('\n');
            j=0;
            while (!weekRings[currentWeekDay][j].isEmpty()) {
                    sendNumber(weekRings[currentWeekDay][j].getSecondFromDayStart());
                    sendText(' ');
                    j++;
            }
            sendText('-');

            sendNumber(currentWeekDay);
            sendText(' ');
            sendNumber(currentTimeSec);
            sendText(' ');
            sendNumber(lastTimeSec);
            sendText(':');
            sendNumber(ringTime);
            sendText(' ');
            sendNumber(i);
            sendText('\n');

	    break;
      }
      i++;
    }
}