package PC_part.SACK_pc_client;

import PC_part.Common.Serial.Serial;
import PC_part.Common.Serial.WiredSerial;
import PC_part.SACK_pc_client.Dialogs.ErrorDialogue;
import PC_part.Common.Logger;

import java.util.ArrayList;

public class DataWrapper {

    private static ArrayList<Ring>[] weekdays;

    private static Serial serial;
    private static String connectionState="Нет соединений";

    public static void init() {
        serial=new WiredSerial();
        weekdays=new ArrayList[7];
        for (int i=0; i<7; i++)
            weekdays[i]=new ArrayList<>();
    }

    public static ArrayList<Ring> getRings(int weekDay) {
        if (serial.getIsConnected()) return weekdays[weekDay]; else
            return new ArrayList<>();
    }

    private static void sortDay(int day) {
        weekdays[day].sort((o1, o2) -> Integer.compare(o1.getSeconds(), o2.getSeconds()));
    }

    private static void removeSame(int day) {
        for (int i=0; i<weekdays[day].size()-1; i++) {
            if (weekdays[day].get(i).getSeconds()==weekdays[day].get(i+1).getSeconds()) {
                weekdays[day].remove(i+1);
            }
        }
    }

    public static void setRings(int weekday, ArrayList<Ring> rings) {
        weekdays[weekday]=rings;
        sortDay(weekday);
        removeSame(weekday);
    }

    public static void addRings(int weekday, ArrayList<Ring> rings) {
        weekdays[weekday].addAll(rings);
        sortDay(weekday);
        removeSame(weekday);
    }

    public static void addRing(int weekday, Ring r) {
        weekdays[weekday].add(r);
        sortDay(weekday);
        removeSame(weekday);
    }

    public static void pop() {


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        String resp = serial.talkWithDuino(Serial.Action.RequestRings, null);

        if (resp==null) return;

        int i=0;
        String[] days = new String[7];
        while (i<7) {

            int index=resp.indexOf("],[");
            if (index==-1) index=resp.length()-1;
            days[i]=(index+3)<resp.length()?resp.substring(0, index).replaceAll("[\\[\\]]", ""):resp;
            resp=(index+3)<resp.length()?resp.substring(index+3):"";

            i++;
        }

        for (i = 0; i < 7; i++) {

            String rings[] = days[i].split(",");

            weekdays[i].clear();
            for (String r : rings) {
                while (r.endsWith("]"))
                    r=r.substring(0, r.length()-1);
                if (r.matches("\\d+"))
                    weekdays[i].add(new Ring(Integer.parseInt(r)));
            }


            sortDay(i);
            removeSame(i);

        }


        Logger.logInfo("DataWrapper", "Got data from duino");

    }

    public static void push() {

        String result="";

        for (int i=0; i<7; i++) {
            if (weekdays[i].size()==0)
                result+=","; else {
                for (Ring s : weekdays[i])
                    result += s.getSeconds() / 10 + ",";
                result=result.substring(0, result.length()-1);
                }
            result+=":";
        }
        result=result.substring(0, result.length()-1);

        serial.talkWithDuino(Serial.Action.SetRings, result);

    }

    public static String[] getPorts() {
        return WiredSerial.getPorts();
    }

    public static void disconnect() {
        if (serial.getIsConnected()) {
            connectionState = "Отсоединение...";
            serial.disconnect();
            connectionState = "Нет соединений";
        }
    }

    public static void connect(String value) {
        if (serial.getIsConnected()) {
            serial.disconnect();
        }

        connectionState="Соединение...";
        serial.connect(value);
        if (serial.getIsConnected()) {
            connectionState="Соединено с "+value;
        } else {
            connectionState = "Не удалось соединиться с " + value;
            processError("Не удалось соединиться с " + value);
        }
    }

    public static String getConnectionState() {
        return connectionState;
    }

    public static boolean getIsConnected() {
        return serial.getIsConnected();
    }

    public static void setTime() {
        serial.talkWithDuino(Serial.Action.SetTime, new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yyyy").format(new java.util.Date()));
    }

    public static int getDuinoTime() {
        String val=serial.talkWithDuino(Serial.Action.RequestTime, null);

        if (val!=null) {
            val = val.substring(0, val.length() - 2);
            return Integer.valueOf(val);
        }
        return -1;
    }

    public static void processError(String text) {
        new ErrorDialogue(text);
    }
}
