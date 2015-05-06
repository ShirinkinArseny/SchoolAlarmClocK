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
    private static final int tries = 10;

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
    abstract void sendBytes(byte[] b);

    /*
            Отправка строки
     */
    abstract void sendString(String s);

    /*
            Чтение массива байт
     */
    abstract byte[] readBytes();

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
            sendBytes(ask);
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void request(String ask) {
        try {
            initConnection();
            sendString(ask);
            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] tryWhile(byte[] request, StringChecker checker) {

        int cycle = 0;

        request(request);
        byte[] resp=new byte[0];

        while (checker.notMatches(resp)) {
            //даём дуине $tries попыток обработать данные корректно
            //(некоторые сообщения приходится читать долго, здесь мы суммируем считанное)

            byte[] newR = readBytes();
            if (newR != null)
                resp =sumArrays(resp, newR);

            cycle++;
            if (cycle > tries) {
                Logger.logError(this.getClass(), "Can't process " + Arrays.toString(request) +
                        "\n         last read: " + Arrays.toString(resp)+
                        "\n         "+new String(resp));
                DataWrapper.processError(Labels.networkErrors);
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.logInfo(this.getClass(), "Processed in " + cycle + " steps");
        return resp;
    }

    private byte[] sumArrays(byte[] a, byte[] b) {
        byte[] c=new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private byte[] tryWhile(String request, StringChecker checker) {

        int cycle = 0;

        request(request);
        byte[] resp=new byte[0];

        while (checker.notMatches(resp)) {
            //даём дуине $tries попыток обработать данные корректно
            //(некоторые сообщения приходится читать долго, здесь мы суммируем считанное)

            byte[] newR = readBytes();
            if (newR != null)
                resp =sumArrays(resp, newR);

            cycle++;
            if (cycle > tries) {
                Logger.logError(this.getClass(), "Can't process " + request +
                        "\n         last read: " + Arrays.toString(resp)+
                        "\n         "+new String(resp));
                DataWrapper.processError(Labels.networkErrors);
                return null;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.logInfo(this.getClass(), "Processed in " + cycle + " steps");
        return resp;
    }

    public enum Action {RequestWeekDay, RequestTime, RequestRings, SetRings, SetTime}

    private static byte[] sum(int c, byte[] b) {
        byte[] res=new byte[b.length+1];
        res[0]= (byte) c;
        System.arraycopy(b, 0, res, 1, b.length);
        return res;
    }

    public synchronized byte[] talkWithDuino(Action act, byte[] s) {

        Logger.logInfo(this.getClass(), "Talking with duino, action: " + act + " request: " + Arrays.toString(s));

        switch (act) {
            case RequestWeekDay: {
                return tryWhile("5", s1 -> s1 == null || !new String(s1).matches("\\d+\\r\\n"));
            }

            case RequestTime: {
                return tryWhile("2", s1 -> s1 == null || !new String(s1).matches("\\d+\\r\\n"));
            }

            case RequestRings: {

                byte[] rings = tryWhile("1", s1 -> s1 == null || !new String(s1).contains("ODone!"));

                if (rings != null)
                    Logger.logInfo(this.getClass(), "Gotta rings: " + Arrays.toString(rings));
                else {
                    Logger.logError(this.getClass(), "Gotta null rings");
                    return null;
                }

                byte[] resultData=new byte[rings.length-6];
                System.arraycopy(rings, 0, resultData, 0, rings.length-6);
                return resultData;
            }

            case SetRings: {


                int index=0;
                byte[] resp = null;
                for (int i=0; i<7; i++) {

                    byte[] links=new byte[s[index]+1];
                    links[0]=s[index];
                    System.arraycopy(s, index+1, links, 1, s[index]);
                    index+=s[index]+1;

                    resp = tryWhile(sum('8', sum(i, links)), s1 -> s1 == null || !new String(s1).contains("SDone!"));
                    Logger.logInfo(this.getClass(), "Updating links! Answer: " + new String(resp));

                }

                byte[] timestamps=new byte[s.length-index];
                System.arraycopy(s, index+1, timestamps, 0, s.length-index-1);

                for (int part=0; part*20<timestamps.length; part++) {


                    byte[] tsPart=new byte[Math.min(20, timestamps.length-part*20)];
                    System.arraycopy(timestamps, part*20, tsPart, 0, Math.min(20, timestamps.length-part*20));


                    resp = tryWhile(sum('3', sum(part, sum(tsPart.length/2, tsPart))), s1 -> s1 == null || !new String(s1).contains("RDone!"));

                    Logger.logInfo(this.getClass(), "Updating timestamps! Answer: " + new String(resp));

                }

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


                        return tryWhile(intsToBytes(bytes), s2 ->  {
                                Logger.logInfo(this.getClass(), "new String(s) : "+new String(s2));
                                return !new String(s2).contains("TDone!");
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

    public interface StringChecker {
        boolean notMatches(byte[] s);
    }

    public abstract boolean getIsConnected();

}