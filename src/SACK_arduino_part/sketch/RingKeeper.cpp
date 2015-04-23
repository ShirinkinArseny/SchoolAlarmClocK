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

#define DELAY_TIME_COEF 2   //длительность стандартного звонка в секундах
#define TIME_SYNC_INTERVAL 60  //время в секундах между проверкой времени

Ring* weekRings; //все звонки сегодня

byte currentWeekDay=0; //текущий день недели

byte ringVoltagePin; //пин, на который подаётся напряжение при звонке

//получаем текущий день недели начиная с нуля (пн)
byte getWeekDay() {
  byte wDay=weekday()-2;
  if (wDay<0) wDay+=7;
  return wDay;
}

long lastTimeSec=0;     //прошлое время от начала дня в секундах
long currentTimeSec=0;  //текущее время от начала дня в секундах

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
  Serial.begin(9600);
  Serial.setTimeout(100);
  ringVoltagePin=ringPin;
  pinMode(ringVoltagePin, OUTPUT);

  //initConnection(13, 12);
  //writeDefaultRings();

  setSyncProvider(RTC.get);
  setSyncInterval(TIME_SYNC_INTERVAL);

  currentWeekDay=getWeekDay();
  weekRings=getDayRings(currentWeekDay);

  lastTimeSec=getCurrentTime();
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
Читаем из соединения новую таблицу звонков
*/
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

        Коды возможных ошибок:
        E1 - Ошибка чтения количества звонков внутри дня
        E2 - Ошибка чтения ссылки на звонок внутри дня
        E3 - Ошибка чтения количества уникальных таймстампов звонков
        E4, E5 - Ошибка чтения первого и второго соответственно байтов таймстампа звонка

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

            writeTimeStampsCountToEEPROM(size);

            /*
                Читаем звонки
            */
            for (byte ring=0; ring<size; ring++) {

                int b1=readValue();
                if (checkForWrongRead(b1, "E4", ring, size)) return;

                int b2=readValue();
                if (checkForWrongRead(b2, "E5", ring, size)) return;

                Ring(256*b1+b2)
                    .writeToEEPROM(ring);
            }

            loadTodayRings();

            //Подтверждение успешной отправки
            Serial.print('R');
            Serial.print(done);
}

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
    RTC.set(now());

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
            case '7':                       //request today's rings
                printTodaysRings();
                break;

        }
    }
}


long ringStartTime=-1;//Время начала подачи напряжения на звонок в секундах от начала дня.
                      //Если -1, значит сейчас ничто не звонит

/*
Подаём звонок
*/
void makeRing() {
  digitalWrite(ringVoltagePin, HIGH);
  ringStartTime=currentTimeSec;
}

/*
В случае завершения звонка передаём подавать напряжение на звонок
*/
void updateRing() {
    if (ringStartTime!=-1) {
        if (currentTimeSec-ringStartTime>DELAY_TIME_COEF) {
            digitalWrite(ringVoltagePin, LOW);
            ringStartTime=-1;
        }
    }
}

/*
Обновление хранителя звонков
*/
void updateRingKeeper() {

    updateRing();

    readCommandsFromSerial();

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
      //Определяем, необходимо ли начинать звонить
      long ringTime=weekRings[i].getSecondFromDayStart();
      if (currentTimeSec>=ringTime && lastTimeSec<ringTime) {
	    makeRing();
	    break;
      }
      i++;
    }
}
