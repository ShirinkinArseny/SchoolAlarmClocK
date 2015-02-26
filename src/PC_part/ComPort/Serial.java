package PC_part.ComPort;


import PC_part.SACK_server_pc_part.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Serial {

    /*
    Количество попыток считать ответ на запрос
     */
    private static final int tries = 100;

    /*
            Начало передачи сообщения
     */
    abstract void initConnection() throws Exception;

    /*
           Конец передачи сообщения
    */
    abstract void closeConnection() throws Exception;

    /*
            Отправка одного байта
     */
    abstract void sendByte(byte b) throws Exception;

    /*
            Отправка строки
     */
    abstract void sendString(String s) throws Exception;

    /*
            Отправка сообщения о завершении передачи
     */
    abstract void sendStop() throws Exception;

    /*
            Чтение строки
     */
    abstract String readString();

    /*
            Установка соединения по его имени
            f.e. в ВайредСериале имя - это /dev/ttyUSB0.

            Устанавливаем соединение в этом методе, а не в конструкторе!
            Экземпляр соединения будет существовать всего один,
            а для переподключений будет использоваться этот метод.
     */
    public abstract void connect(String name);

    /*
            Закрытие существующего соединения
            (например, для корректного переподключения).
     */
    public abstract void disconnect();

    public String request(ArrayList<Short> ask) {
        try {
            initConnection();
            for (Short anAsk : ask) sendByte(anAsk.byteValue());
            sendStop();
            Thread.sleep(10);
            String result = readString();
            closeConnection();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String request(String ask) {
        try {
            initConnection();
            sendString(ask);
            sendStop();
            Thread.sleep(10);
            String result = readString();
            closeConnection();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String tryWhile(ArrayList<Short> request, StringChecker checker) {

        int cycle = 0;

        String resp = request(request);

        while (!checker.matches(resp)) {
            //даём дуине $tries попыток обработать данные корректно
            //(некоторые сообщения приходится читать долго, здесь мы суммируем считанное)

            String newR = readString();
            if (newR != null)
                resp += newR;

            cycle++;
            if (cycle > tries) {
                Logger.logError("Serial", "Can't process " + request);
                break;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.logInfo("Serial", "Processed in " + cycle + " steps");
        return resp;
    }

    private String tryWhile(String request, StringChecker checker) {

        int cycle = 0;

        String resp = request(request);
        if (resp == null)
            resp = "";

        while (!checker.matches(resp)) {
            //даём дуине $tries попыток обработать данные корректно
            //(некоторые сообщения приходится читать долго, здесь мы суммируем считанное)

            String newR = readString();
            if (newR != null)
                resp += newR;

            cycle++;
            if (cycle > tries) {
                Logger.logError("Serial", "Can't process " + request + "\n       last readed: " + resp);
                return null;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.logInfo("Serial", "Processed in " + cycle + " steps");
        return resp;
    }

    public enum Action {RequestWeekDay, RequestTime, RequestRings, SetRings, SetTime}

    public synchronized String talkWithDuino(Action act, String s) {
        switch (act) {
            case RequestWeekDay:
                return request("5");

            case RequestTime: {
                return tryWhile("2", new StringChecker() {
                    @Override
                    boolean matches(String s) {
                        return s != null && s.matches("\\d+\\r\\n");
                    }
                });
            }

            case RequestRings: {

                return tryWhile("1", new StringChecker() {
                    @Override
                    boolean matches(String s) {
                        return s != null && s.matches("\\[\\[.*]]\\r\\n");
                    }
                });
            }

            case SetRings: {

                /*
                    Входная строка вида 25,30,1002:,:,:14,16,28:,:,:,
                    Переводим её в массив байт вида
                        количество звонков в понедельник
                            первый байт первого звонка
                            второй байт первого звонка
                            первый байт второго звонка
                            второй байт второго звонка
                            ...
                        количество звонков во вторник
                            ...
                        ...
                 */

                //WARNING! Храним в short, так лучше стыкуются unsigned байты Си и signed байты Java
                ArrayList<Short> bytes = new ArrayList<>();
                bytes.add((short) '3');
                String[] times = s.split(":");
                for (int day = 0; day < 7; day++) {

                    String[] rings = times[day].split(",");
                    bytes.add((short) (rings.length));//TODO: В СЕРВЕРЕ ПЕРЕДЕЛАТЬ
                    for (String ring : rings) {
                        int value = Integer.valueOf(ring);
                        bytes.add((short) (value / 256));
                        bytes.add((short) (value % 256));
                    }

                }

                String resp = tryWhile(bytes, new StringChecker() {
                    @Override
                    boolean matches(String s) {
                        return s != null && s.contains("RDone!");
                    }
                });

                Logger.logInfo("Serial", "Printing table to EEPROM! Answer: " + resp);
                return resp;
            }
            case SetTime: {

                String[] splitted = s.split(":");
                if (splitted.length == 6) {
                    int hour = Integer.valueOf(splitted[0]);
                    int minute = Integer.valueOf(splitted[1]);
                    int second = Integer.valueOf(splitted[2]);
                    int day = Integer.valueOf(splitted[3]);
                    int month = Integer.valueOf(splitted[4]);
                    int year = Integer.valueOf(splitted[5]);

                    if
                            (hour >= 0 && hour < 24 &&
                            minute >= 0 && minute < 60 &&
                            second >= 0 && second < 60 &&
                            day >= 1 && day <= 31 &&
                            month >= 1 && month <= 12 &&
                            year >= 1970 && year < 2030
                            ) {


                        ArrayList<Short> bytes = new ArrayList<>();
                        bytes.add((short) '6');
                        bytes.add((short) (hour % 256));
                        bytes.add((short) (minute % 256));
                        bytes.add((short) (second % 256));
                        bytes.add((short) (day % 256));
                        bytes.add((short) (month % 256));
                        bytes.add((short) (year / 256));
                        bytes.add((short) (year % 256));


                        return tryWhile(bytes, new StringChecker() {
                            @Override
                            boolean matches(String s) {
                                return s != null && s.contains("TDone!");
                            }
                        });
                    }
                    Logger.logError("Serial", "Wrong time string: " + Arrays.toString(splitted) + " - values are not in bounds");
                }
                Logger.logError("Serial", "Wrong time string: " + Arrays.toString(splitted) + " - not 6 args");
                return null;
            }
        }
        return null;
    }

    abstract class StringChecker {
        abstract boolean matches(String s);
    }

    public abstract boolean getIsConnected();

}