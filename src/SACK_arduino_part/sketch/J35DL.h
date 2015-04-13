#ifndef J35DB_header_check
#define J35DB_header_check

#include <Arduino.h>

/*
  Не велосипед, а арт-объект.

  Простая библиотека для вывода текстовых сообщений на компьютер
  Три контакта *дуино подключаются к трём контактам звуковой карты
  Левый канал служит для передачи данных, а правый для передачи тактового сигнала
  На компьютере должна быть запущена PC_part.J35DB_pc_part

  Использование:
  
  startSending();
  
  sendText(your data #1);
  sendNumber(your data #2);
  ...
  sendChar(your data #n);
  
  stopSending();

*/

/*
  Чем больше значения - тем выше точность и меньше скорость передачи. Подстраивать под Ваши конкретные нужды
*/
#define syncDelay 150               //  Задержка между передаваемыми битами в микросекундах (10^-6)
#define preSyncDelay 150            //  Задержка между передачей бита и тактовым сигналом в микросекундах (10^-6)
#define afterMessageDelay 200       //  Задержка после передачи сообщения в милисекундах (10^-3)

void sendText(char msg);            //  Отправка одного символа

void sendText(char* msg);           //  Отправка текста

void sendText(String msg);          //  Отправка текста

void sendNumber(long value);        //  Отправка числа

void startSending();                //  Начало передачи

void stopSending();                 //  Завершение передачи

/*
  Установка соединения. Sync - правый аудиоканал для тактовой частоты, channel - левый аудиоканал для передачи информации
*/
void initConnection(int sync, int channel);

#endif