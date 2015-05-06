package PC_part.SACK_pc_client;

import PC_part.Common.Serial.Serial;
import PC_part.Common.Serial.WiredSerial;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Dialogs.ErrorDialogue;
import PC_part.Common.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static PC_part.SACK_pc_client.PlayingAroundBytes.bytesToInts;
import static PC_part.SACK_pc_client.PlayingAroundBytes.intsToBytes;

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


        byte[] resp = serial.talkWithDuino(Serial.Action.RequestRings, null);

        deSerializeTable(resp);
        Logger.logInfo(DataWrapper.class, "Got data from duino");

    }

    public static void deSerializeTable(byte[] table) {

        try {

            Logger.logInfo(DataWrapper.class, "Data to deserialize: "+ Arrays.toString(table));

            int[] data = bytesToInts(table);

            Logger.logInfo(DataWrapper.class, "Normalised data to deserialize: "+ Arrays.toString(data));

            int index = -1;

            ArrayList<Integer>[] ringsRefs = new ArrayList[7];
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
                for (int ringRef : ringsRefs[day]) {
                    ringsNew[day].add(new Ring(rings.get(ringRef)));
                }
                ringsNew[day].sort((o1, o2) -> Integer.compare(o1.getSeconds(), o2.getSeconds()));
            }

            weekdays = ringsNew;
        }
        catch (Exception e) {
            DataWrapper.processError(Labels.errorsInTableFile);
            e.printStackTrace();
        }
    }

    public static byte[] getSerializedTable() {

        Logger.logInfo(DataWrapper.class, "Starting serialization!");

        ArrayList<Integer> bytes = new ArrayList<>();// структура: ссылки на звонки+звонки

        ArrayList<Integer> realRings = new ArrayList<>();//реальные звонки, на которые будем впоследствие ссылаться

        for (int day = 0; day < 7; day++) {

            bytes.add(weekdays[day].size());
            for (Ring ring : weekdays[day]) {

                int value = ring.getArduinoMemoryRepresentation();

                int index = realRings.indexOf(value);
                if (index == -1) {
                    bytes.add(realRings.size());
                    realRings.add(value);
                } else {
                    bytes.add(index);
                }
            }
        }
        Logger.logInfo(DataWrapper.class, "Links: " + Arrays.toString(bytes.toArray()));

        bytes.add(realRings.size());
        for (Integer i : realRings) {
            bytes.add(i / 256);
            bytes.add(i % 256);
        }
        Logger.logInfo(DataWrapper.class, "Full data: " + Arrays.toString(bytes.toArray()));

        return intsToBytes(bytes);
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

    /*private static String ipToString(int[] ip) {
        return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
    }*/

    /*public static void connect(int[] ip) {
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
    }*/

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
        serial.talkWithDuino(Serial.Action.SetTime,
                new java.text.SimpleDateFormat("HH:mm:ss:dd:MM:yyyy").format(new java.util.Date()).getBytes());
    }

    public static void pingDuino() {
        serial.initConnection();
        serial.request("2");
        serial.closeConnection();
    }

    public static int getDuinoTime() {
        byte[] val = serial.talkWithDuino(Serial.Action.RequestTime, null);

        if (val != null) {
            return Integer.valueOf(new String(val).substring(0, val.length - 2));
        }
        return -1;
    }

    public static void processError(String text) {
        new ErrorDialogue(text);
    }
}
