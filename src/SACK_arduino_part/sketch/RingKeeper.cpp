#include <Arduino.h>

#include "J35DL.h"
#include "Ring.h"
#include <Time.h>
#include <DS1307RTC.h>
#include <Wire.h>

/*
Текущее время в секундах от начала дня
*/
long getCurrentTime() {
    return 3600L*hour()+60L*minute()+second();
}

#define DELAY_TIME_COEF 5500   //длительность стандартного звонка в миллисекундах
#define SHORT_DELAY_TIME_COEF 300   //длительность короткого звонка в миллисекундах
#define TIME_SYNC_INTERVAL 10  //время в секундах между проверками точности времени

#define EXTERNAL_HARDWARE_CLOCK

Ring* weekRings; //все звонки сегодня

byte currentWeekDay=0; //текущий день недели

byte ringVoltagePin; //пин, на который подаётся напряжение при звонке

boolean currentRingIsShort=false;

//получаем текущий день недели начиная с нуля (пн)
byte getWeekDay() {
  byte wDay=weekday()-2;
  if (wDay<0) wDay+=7;
  return wDay;
}

long lastTimeSec;     //прошлое время от начала дня в секундах
long currentTimeSec;  //текущее время от начала дня в секундах

/*
Загружаем сегодняшние звонки в память и удаляем старые
*/
void loadTodayRings() {
             delete(weekRings);
             weekRings=getDayRings(currentWeekDay);
}

/*
Инициализируем хранитель звонков. ringPin - пин, на который будет подаваться напряжение при звонке
*/
void initRingKeeper(byte ringPin) {
  Serial.begin(115200);
  Serial.setTimeout(1000);
  ringVoltagePin=ringPin;
  pinMode(ringVoltagePin, OUTPUT);
  digitalWrite(ringVoltagePin, LOW); //На всякий случай

  //initConnection(13, 12);
  //writeDefaultRings();

   #ifdef EXTERNAL_HARDWARE_CLOCK
   Wire.begin();
   #endif

  currentWeekDay=getWeekDay();
  weekRings=getDayRings(currentWeekDay);

  currentTimeSec=getCurrentTime();
  lastTimeSec=currentTimeSec;
}

/*
Возвращаем количество свободной оперативки
*/
int getFreeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

/*
Печатаем количество свободной оперативки
*/
void printFreeMemory() {
    Serial.print("FREEMEM: ");
    Serial.print(getFreeRam());
    Serial.print("\n");
}

#define READ_BYTE_TRIES_NUMBER 100 //количество попыток считать байт

/*
Читаем байт из соединения.
Если байт считан некорректно - предпринимаем несколько дополнительных попыток чтения.
В случае отсутствия данных возвращаем -1.
*/
int readValue() {
    int value=Serial.read();

    byte cyc=0;

    while (value<0 && cyc<READ_BYTE_TRIES_NUMBER) {
            delay(100);
            cyc++;
            value=Serial.read();
    }



    return value;
}

/*
Проверяем введённые данные на корректность.
В случае ошибки выводим сообщение о ней.
value - значение для проверки
error - текст ошибки
day - день недели, в котором произошла ошибка
ring - номер звонка, в котором произошла ошибка
*/
boolean checkForWrongRead(int value, String error, int code1, int code2) {
    if (value<0) {
             Serial.print(error);
             Serial.print(':');
             Serial.print(code1);
             Serial.print(':');
             Serial.println(code2);
             loadTodayRings();
             return true;
    }
    Serial.print(value);
    Serial.print(", ");
    return false;
}

String done="Done!";

/*
Читаем звонки
*/
bool readNewTimestamps() {


            byte part=readValue();
            if (checkForWrongRead(part, "E6", part, part)) return false;

            int size=readValue(); //Количество звонков
            if (checkForWrongRead(size, "E3", size, part)) return false;

            writeTimeStampsCountToEEPROM(size+part*10);

            for (byte ring=0; ring<size; ring++) {

                int b1=readValue();
                if (checkForWrongRead(b1, "E4", ring, part)) return false;

                int b2=readValue();
                if (checkForWrongRead(b2, "E5", ring, part)) return false;

                Ring(256*b1+b2)
                    .writeToEEPROM(ring+part*10);
            }

            loadTodayRings();

            //Подтверждение успешной отправки
            Serial.print('R');
            Serial.print(done);
            return true;
}

/*
Читаем ссылки на звонки
*/
bool readNewLinks() {
            byte day=readValue();
            if (checkForWrongRead(day, "E0", day, -1)) return false;

                int size=readValue(); //Количество звонков в day-ый день
                if (checkForWrongRead(size, "E1", day, -1)) return false;

                writeDayRingsNumberToEEPROM(day, size);

                for (byte ring=0; ring<size; ring++) {

                    int link=readValue();
                    if (checkForWrongRead(link, "E2", day, ring)) return false;
                    writeDayRingToEEPROM(day, ring, link);
                }

            //Подтверждение успешной отправки
            Serial.print('S');
            Serial.print(done);
            return true;
}

/*
Читаем из соединения новую таблицу звонков
*/

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

        Коды возможных ошибок:
        E1 - Ошибка чтения количества звонков внутри дня
        E2 - Ошибка чтения ссылки на звонок внутри дня
        E3 - Ошибка чтения количества уникальных таймстампов звонков
        E4, E5 - Ошибка чтения первого и второго соответственно байтов таймстампа звонка

        */

/*
Пишем текущую таблицу звонков в соединение.
*/
void printRingsTableToSerial() {

    for (byte day=0; day<7; day++) {
        byte* dayLinks=getDayMemoryRepresentation(day);
        for (byte i=0; i<=dayLinks[0]; i++) {
            Serial.print((char)dayLinks[i]);
        }
        delete(dayLinks);
    }

    byte number=getRingsTimeStampsNumber();
    Serial.print((char)number);
    for (int i=0; i<number; i++) {
        Serial.print((char)getRingTimeStamp(i, 0));
        Serial.print((char)getRingTimeStamp(i, 1));
    }

    //Подтверждение успешной отправки
    Serial.print('O');
    Serial.print(done);

    loadTodayRings();
}

/*
Читаем из соединения новое время.
*/
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

    #ifdef EXTERNAL_HARDWARE_CLOCK
    RTC.set(now());
    #endif

    //Подтверждение успешной отправки
    Serial.print('T');
    Serial.print(done);

    currentTimeSec=getCurrentTime();
    lastTimeSec=currentTimeSec-1;
    currentWeekDay=getWeekDay();

    loadTodayRings();
}

/*
Печатаем в соединение список сегодняшних звонков
*/
void printTodaysRings() {
    Serial.print("CTS: ");
    Serial.print(currentTimeSec);
    Serial.print(" RINGS: ");
    byte i=0;
    while (!weekRings[i].isEmpty()) {
      long ringTime=weekRings[i].getSecondFromDayStart();
      Serial.print(ringTime);
      Serial.print(", ");
      i++;
    }
    Serial.print('P');
    Serial.print(done);
    Serial.print('\n');
}

/*
Читаем команду из соединения. Если команда успешно считана- выполняем её.
*/
void readCommandsFromSerial() {

    int readed=Serial.read();

    if (readed!=-1) {

        switch (readed) {

            case '1':                       //get rings table
                printRingsTableToSerial();
                break;
            case '2':                       //get current time
                Serial.println(currentTimeSec);
                break;
            case '3':                       //set rings stamps
                readNewTimestamps();
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
            case '7':                       //request today's rings
                printTodaysRings();
                break;
            case '8':                       //set rings links
                readNewLinks();
                break;

        }
    }
}


unsigned long ringStartTime=-1;//Время начала подачи напряжения на звонок
boolean nowIsRinging=false;//Происходит ли звонок сейчас

/*
Подаём звонок
*/
void makeRing() {
  digitalWrite(ringVoltagePin, HIGH);
  ringStartTime=millis();
  nowIsRinging=true;
}

/*
Останавливаем звонок
*/
void stopRing() {
    digitalWrite(ringVoltagePin, LOW);
    nowIsRinging=false;
}

/*
В случае завершения звонка передаём подавать напряжение на звонок
*/
void updateRing() {
        if (
            (currentRingIsShort && millis()-ringStartTime>SHORT_DELAY_TIME_COEF)
                ||
            (millis()-ringStartTime>DELAY_TIME_COEF)
        ) {
            stopRing();
        }
}

long getMax(long a, long b) {
    return a>b?a:b;
}

/*
Обновление хранителя звонков
*/
void updateRingKeeper() {


    #ifdef EXTERNAL_HARDWARE_CLOCK
    setTime(RTC.get());
    #endif

    lastTimeSec=currentTimeSec;
    currentTimeSec=getCurrentTime();

    //полночь, переход между днями
    if (currentWeekDay!=getWeekDay()) {
        lastTimeSec=0;
        currentWeekDay=getWeekDay();
        loadTodayRings();
        return;
    }

    //тот самый момент, когда в следующий момент времени количество секунд с начала дня меньше, чем сейчас
    //Welcome to past!
    if (currentTimeSec<lastTimeSec) {
         //сглаживание неравномерности времени
         currentTimeSec=lastTimeSec;
    }


    if (nowIsRinging) {
        updateRing();
    }

    readCommandsFromSerial();

    byte i=0;
    while (!weekRings[i].isEmpty()) {
      //Определяем, необходимо ли начинать звонить
      long ringTime=weekRings[i].getSecondFromDayStart();
      if (currentTimeSec>=ringTime && lastTimeSec<ringTime) {
        currentRingIsShort=weekRings[i].isShort();
	    makeRing();
	    break;
      }
      i++;
    }

    delay(100);
}
