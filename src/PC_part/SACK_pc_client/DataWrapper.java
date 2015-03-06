package PC_part.SACK_pc_client;

import PC_part.Common.Serial.ESPSerial;
import PC_part.Common.Serial.Serial;
import PC_part.Common.Serial.WiredSerial;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Dialogs.ErrorDialogue;
import PC_part.Common.Logger;

import java.util.ArrayList;

public class DataWrapper {

    private static ArrayList<Ring>[] weekdays;

    private static Serial serial;
    private static String connectionState = Labels.noConnections;

    public static void init() {
        serial = new WiredSerial();
        weekdays = new ArrayList[7];
        for (int i = 0; i < 7; i++)
            weekdays[i] = new ArrayList<>();
    }

    public static ArrayList<Ring> getRings(int weekDay) {
        return weekdays[weekDay];
    }

    private static void sortDay(int day) {
        weekdays[day].sort((o1, o2) -> Integer.compare(o1.getSeconds(), o2.getSeconds()));
    }

    private static void removeSame(int day) {
        for (int i = 0; i < weekdays[day].size() - 1; i++) {
            if (weekdays[day].get(i).getSeconds() == weekdays[day].get(i + 1).getSeconds()) {
                weekdays[day].remove(i + 1);
            }
        }
    }

    public static void setRings(int weekday, ArrayList<Ring> rings) {
        weekdays[weekday] = rings;
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

        if (resp == null) return;

        int i = 0;
        String[] days = new String[7];
        while (i < 7) {

            int index = resp.indexOf("],[");
            if (index == -1) index = resp.length() - 1;
            days[i] = (index + 3) < resp.length() ? resp.substring(0, index).replaceAll("[\\[\\]]", "") : resp;
            resp = (index + 3) < resp.length() ? resp.substring(index + 3) : "";

            i++;
        }

        for (i = 0; i < 7; i++) {

            String rings[] = days[i].split(",");

            weekdays[i].clear();
            for (String r : rings) {
                while (r.endsWith("]"))
                    r = r.substring(0, r.length() - 1);
                if (r.matches("\\d+"))
                    weekdays[i].add(new Ring(Integer.parseInt(r)));
            }


            sortDay(i);
            removeSame(i);

        }


        Logger.logInfo("DataWrapper", "Got data from duino");

    }

    public static void deSerializeTable(String table) {

        try {


            short[] data = new short[table.length()];
            for (int i = 0; i < data.length; i++)
                data[i] = (short) table.charAt(i);


            int index = -1;

            ArrayList<Short>[] ringsRefs = new ArrayList[7];
            ArrayList<Integer> rings = new ArrayList<>();

            for (int day = 0; day < 7; day++) {
                int size = data[++index];
                ringsRefs[day] = new ArrayList<>(size);
                for (int ring = 0; ring < size; ring++) {
                    ringsRefs[day].add(data[++index]);
                }
            }
            int ringsNumber = data[++index];
            for (int ring = 0; ring < ringsNumber; ring++) {

                int newRing = data[++index];
                newRing *= 256;
                newRing += data[++index];
                rings.add(newRing);

            }

            ArrayList<Ring>[] ringsNew = new ArrayList[7];
            for (int day = 0; day < 7; day++) {
                ringsNew[day] = new ArrayList<>(ringsRefs[day].size());
                for (short ringRef : ringsRefs[day]) {
                    ringsNew[day].add(new Ring(rings.get(ringRef) * 10));
                }
            }

            weekdays = ringsNew;
        }
        catch (Exception e) {
            DataWrapper.processError(Labels.errorsInTableFile);
        }
    }

    public static String getSerializedTable() {
        ArrayList<Short> bytes = new ArrayList<>();

        ArrayList<Integer> realRings = new ArrayList<>();

        for (int day = 0; day < 7; day++) {

            bytes.add((short) (weekdays[day].size()));
            for (Ring ring : weekdays[day]) {
                int value = ring.getSeconds() / 10;

                int index = realRings.indexOf(value);
                if (index == -1) {
                    bytes.add((short) realRings.size());
                    realRings.add(value);
                } else {
                    bytes.add((short) index);
                }
            }
        }

        bytes.add((short) realRings.size());
        for (Integer i : realRings) {
            bytes.add((short) (i / 256));
            bytes.add((short) (i % 256));
        }

        StringBuilder sb = new StringBuilder();
        for (short s : bytes)
            sb.append((char) s);

        return sb.toString();
    }

    public static void push() {
        serial.talkWithDuino(Serial.Action.SetRings, getSerializedTable());

    }

    public static String[] getPorts() {
        return WiredSerial.getPorts();
    }

    public static void disconnect() {
        if (serial.getIsConnected()) {
            connectionState = Labels.disconnection;
            serial.disconnect();
            connectionState = Labels.noConnections;
        }
    }

    private static String ipToString(int[] ip) {
        return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
    }

    public static void connect(int[] ip) {
        if (serial.getIsConnected()) {
            serial.disconnect();
        }

        connectionState = Labels.connectionNow;
        serial = new ESPSerial();

        serial.connect(ipToString(ip));
        if (serial.getIsConnected()) {
            connectionState = Labels.connectedTo + ipToString(ip);
        } else {
            connectionState = Labels.cannotConnectTo + ipToString(ip);
            processError(Labels.cannotConnectTo + ipToString(ip));
        }
    }

    public static void connect(String value) {
        if (serial.getIsConnected()) {
            serial.disconnect();
        }

        serial = new WiredSerial();

        connectionState = Labels.connectionNow;
        serial.connect(value);
        if (serial.getIsConnected()) {
            connectionState = Labels.connectedTo + value;
        } else {
            connectionState = Labels.cannotConnectTo + value;
            processError(Labels.cannotConnectTo + value);
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

    public static void pingDuino() {
        serial.initConnection();
        serial.request("2");
        serial.closeConnection();
    }

    public static int getDuinoTime() {
        String val = serial.talkWithDuino(Serial.Action.RequestTime, null);

        if (val != null) {
            val = val.substring(0, val.length() - 2);
            return Integer.valueOf(val);
        }
        return -1;
    }

    public static void processError(String text) {
        new ErrorDialogue(text);
    }
}
