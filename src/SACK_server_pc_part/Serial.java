package SACK_server_pc_part;


import java.util.ArrayList;

public abstract class Serial {

    abstract void initConnection() throws Exception;
    abstract void closeConnection() throws Exception;
    abstract void sendByte(byte b) throws Exception;
    abstract void sendString(String s) throws Exception;
    abstract void sendStop() throws Exception;
    abstract String readString() throws Exception;

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
}