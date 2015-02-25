package PC_part.ComPort;

import PC_part.SACK_server_pc_part.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Serial {

    public enum Action {RequestWeekDay, RequestTime, RequestRings, SetRings, SetTime}
    public synchronized String talkWithDuino(Action act, String s) {
        switch (act) {
            case RequestWeekDay: return request("5");
            case RequestTime: return request("2");
            case RequestRings: return request("1");
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
                ArrayList<Short> bytes=new ArrayList<>();
                bytes.add((short) '3');
                String[] times=s.split(":");
                for (int day=0; day<7; day++) {

                        String[] rings = times[day].split(",");
                        bytes.add((short) (rings.length));//TODO: В СЕРВЕРЕ ПЕРЕДЕЛАТЬ
                    for (String ring : rings) {
                        int value = Integer.valueOf(ring);
                        bytes.add((short) (value / 256));
                        bytes.add((short) (value % 256));
                    }

                }

                String resp=request(bytes);//отсылаем байты на дуину
                int cycle=0;

                while (resp==null || !resp.contains("RDone!")) {//даём дуине 50 дополнительных попыток обработать данные корректно
                    resp=request(bytes);
                    cycle++;
                    if (cycle>50) {
                        //50 неправильных попыток
                        //TODO: обработать эту ошибку
                        Logger.logError("DUINOTALK", "Can't write ringtable");
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                Logger.logInfo("DUINOTALK", "Printing table to EEPROM! Answer: " + resp);
                return null;
            }
            case SetTime: {

                String[] splitted=s.split(":");
                if (splitted.length==6) {
                    int hour=Integer.valueOf(splitted[0]);
                    int minute=Integer.valueOf(splitted[1]);
                    int second=Integer.valueOf(splitted[2]);
                    int day=Integer.valueOf(splitted[3]);
                    int month=Integer.valueOf(splitted[4]);
                    int year=Integer.valueOf(splitted[5]);

                    if
                            (hour>=0 && hour<24 &&
                            minute>=0 && minute<60 &&
                            second>=0 && second<60 &&
                            day>=1 && day<=31 &&
                            month>=1 && month<=12 &&
                            year>=1970 && year<2030
                            ) {


                        ArrayList<Short> bytes=new ArrayList<>();
                        bytes.add((short) '6');
                        bytes.add((short) (hour%256));
                        bytes.add((short) (minute%256));
                        bytes.add((short) (second%256));
                        bytes.add((short) (day%256));
                        bytes.add((short) (month%256));
                        bytes.add((short) (year/256));
                        bytes.add((short) (year%256));
                        Logger.logInfo("DUINOTALK", "Input: " + s);
                        Logger.logInfo("DUINOTALK", "Sent: " + bytes);
                        String resp=request(bytes);
                        Logger.logInfo("DUINOTALK", "Responsed: " + resp);
                        return resp;
                    }
                    Logger.logError("DUINOTALK", "Wrong time string: "+ Arrays.toString(splitted) +" - values are not in bounds");
                }
                Logger.logError("DUINOTALK", "Wrong time string: "+ Arrays.toString(splitted) +" - not 6 args");
                return null;
            }
        }return null;
    }

    private static SerialPort serialPort;

    public String request(ArrayList<Short> ask) {
        try {
            for (Short anAsk : ask) serialPort.writeByte(anAsk.byteValue());
            serialPort.writeByte((byte) '\n');
            serialPort.writeByte((byte) '\r');
            Thread.sleep(10);
            return serialPort.readString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String request(String ask) {
        try {
            serialPort.writeString(ask+"\n\r");
            Thread.sleep(10);
            return serialPort.readString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean connected=false;

    public boolean getIsConnected() {
        return connected;
    }

    public boolean connect(String name) {try {
        serialPort = new SerialPort(name);
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                SerialPort.FLOWCONTROL_RTSCTS_OUT);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Logger.logInfo("Serial", "Connected to serial on " + name);
        connected=true;
        return true;
    } catch (SerialPortException e) {
        Logger.logError("Serial", "Can't connect to serial on "+name+", cuz "+e.toString());
        connected=false;
        return false;
    }
    }


    public void disconnect() {
        try {
            serialPort.closePort();
            Logger.logInfo("Serial", "Disconnected from serial");
        } catch (SerialPortException e) {
            Logger.logError("Serial", "Can't disconnect from serial, cuz " + e.toString());
        }
        connected=false;
    }

    public String[] getPorts() {
        return SerialPortList.getPortNames();
    }

    public Serial() {
    }
}