/*
package PC_part.Common.Serial;

import java.io.IOException;
import java.net.Socket;

public class ESPSerial extends Serial {
    private Socket espSocket;
    private String ipESP;
    private int port;
    private byte[] buffer;

    public ESPSerial() {
        //TODO
    }

    @Override
    public void initConnection() {
        try {
            espSocket = new Socket(ipESP, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            espSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void sendByte(byte b) {
        try {
            espSocket.getOutputStream().write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void sendString(String s) {
        try {
            espSocket.getOutputStream().write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    String readString() {
        int r;
        try {
            r = espSocket.getInputStream().read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(buffer,0,r);
    }

    @Override
    public void connect(String name) {
        //TODO
    }

    @Override
    public void disconnect() {
        //TODO
    }

    @Override
    public boolean getIsConnected() {
        //TODO
        return false;
    }

    private void setAddress(String ipESP, int port) {
        this.ipESP = ipESP;
        this.port = port;
    }

    private ESPSerial(String ipESP, int port, int maxStringBuffer) {
        setAddress(ipESP,port);
        buffer = new byte[maxStringBuffer];
    }

    public ESPSerial(String ipESP, int port) {
        this(ipESP, port, 64*1024);
    }
}
*/
