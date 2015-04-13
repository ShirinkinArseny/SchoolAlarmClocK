package PC_part.Common.Serial;


import PC_part.Common.Logger;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.DataWrapper;

import java.util.ArrayList;
import java.util.Arrays;

import static PC_part.SACK_pc_client.PlayingAroundBytes.intsToBytes;

public abstract class Serial {

    /*
    Количество попыток считать ответ на запрос
     */
    private static final int tries = 100;

    /*
            Начало передачи сообщения
     */
    public abstract void initConnection();

    /*
           Конец передачи сообщения
    */
    public abstract void closeConnection();

    /*
            Отправка одного байта
     */
    abstract void sendByte(byte b);

    /*
            Отправка строки
     */
    abstract void sendString(String s);

    /*
            Отправка сообщения о завершении передачи
     */
    private void sendStop() {
        sendByte((byte) '\n');
        sendByte((byte) '\r');
    }

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

    private void request(byte[] ask) {
        try {
            initConnection();
            for (byte anAsk : ask) sendByte(anAsk);
            sendStop();
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request(String ask) {
        try {
            initConnection();
            sendString(ask + "\r\n");
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String tryWhile(byte[] request, StringChecker checker) {

        int cycle = 0;

        request(request);
        String resp = "";

        while (checker.notMatches(resp)) {
            //даём дуине $tries попыток обработать данные корректно
            //(некоторые сообщения приходится читать долго, здесь мы суммируем считанное)

            String newR = readString();
            if (newR != null)
                resp += newR;

            cycle++;
            if (cycle > tries) {
                Logger.logError(this.getClass(), "Can't process " + Arrays.toString(request) + "\n       last read: " + resp);
                DataWrapper.processError(Labels.networkErrors);
                break;
            }
        }
        Logger.logInfo(this.getClass(), "Processed in " + cycle + " steps");
        return resp;
    }

    private String tryWhile(String request, StringChecker checker) {

        int cycle = 0;

        request(request);
        String resp = "";

        while (checker.notMatches(resp)) {
            //даём дуине $tries попыток обработать данные корректно
            //(некоторые сообщения приходится читать долго, здесь мы суммируем считанное)

            String newR = readString();
            if (newR != null)
                resp += newR;

            cycle++;
            if (cycle > tries) {
                Logger.logError(this.getClass(), "Can't process " + request + "\n       last read: " + resp);
                DataWrapper.processError(Labels.networkErrors);
                return null;
            }
        }
        Logger.logInfo(this.getClass(), "Processed in " + cycle + " steps");
        return resp;
    }

    public enum Action {RequestWeekDay, RequestTime, RequestRings, SetRings, SetTime}

    private static byte[] sum(char c, byte[] b) {
        byte[] res=new byte[b.length+1];
        res[0]= (byte) c;
        System.arraycopy(b, 0, res, 1, b.length);
        return res;
    }

    public synchronized String talkWithDuino(Action act, byte[] s) {

        Logger.logInfo(this.getClass(), "Talking with duino, action: " + act + " request: " + s);

        switch (act) {
            case RequestWeekDay: {
                return tryWhile("5", new StringChecker() {
                    @Override
                    boolean notMatches(String s) {
                        return s == null || !s.matches("\\d+\\r\\n");
                    }
                });
            }

            case RequestTime: {
                return tryWhile("2", new StringChecker() {
                    @Override
                    boolean notMatches(String s) {
                        return s == null || !s.matches("\\d+\\r\\n");
                    }
                });
            }

            case RequestRings: {

                String rings = tryWhile("1", new StringChecker() {
                    @Override
                    boolean notMatches(String s) {
                        return s == null || !s.matches("\\[\\[.*]]\\r\\n");
                    }
                });

                if (rings != null)
                    Logger.logInfo(this.getClass(), "Gotta rings: " + rings.replaceAll("\\n", ""));
                else
                    Logger.logError(this.getClass(), "Gotta null rings");
                return rings;
            }

            case SetRings: {

                String resp = tryWhile(sum('3', s), new StringChecker() {
                    @Override
                    boolean notMatches(String s) {
                        return s == null || !s.contains("RDone!");
                    }
                });

                Logger.logInfo(this.getClass(), "Printing table to EEPROM! Answer: " + resp);
                return resp;
            }
            case SetTime: {

                String[] splitted = new String(s).split(":");
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


                        ArrayList<Integer> bytes = new ArrayList<>();
                        bytes.add((int) '6');
                        bytes.add(hour % 256);
                        bytes.add(minute % 256);
                        bytes.add(second % 256);
                        bytes.add(day % 256);
                        bytes.add(month % 256);
                        bytes.add(year / 256);
                        bytes.add(year % 256);


                        return tryWhile(intsToBytes(bytes), new StringChecker() {
                            @Override
                            boolean notMatches(String s) {
                                return s == null || !s.contains("TDone!");
                            }
                        });
                    }
                    Logger.logError(this.getClass(), "Wrong time string: " + Arrays.toString(splitted) + " - values are not in bounds");
                }
                Logger.logError(this.getClass(), "Wrong time string: " + Arrays.toString(splitted) + " - not 6 args");
                return null;
            }
        }
        return null;
    }

    abstract class StringChecker {
        abstract boolean notMatches(String s);
    }

    public abstract boolean getIsConnected();

}