package SACK_server_pc_part;

import sun.rmi.runtime.Log;

import java.net.Socket;

public class ESPSerial extends Serial {
    private Socket espSocket;
    private String ipESP;
    private int port;
    byte[] buffer;

    @Override
    void initConnection() throws Exception {
        espSocket = new Socket(ipESP, port);
    }

    @Override
    void closeConnection() throws Exception {
        espSocket.close();
    }

    @Override
    void sendByte(byte b) throws Exception {
        espSocket.getOutputStream().write(b);
    }

    @Override
    void sendString(String s) throws Exception {
        espSocket.getOutputStream().write(s.getBytes());
    }

    @Override
    void sendStop() throws Exception {
        sendString("\r\n");
    }

    @Override
    String readString() throws Exception {
        int r = espSocket.getInputStream().read(buffer);
        String s = new String(buffer,0,r);
        return new String(buffer,0,r);
    }

    public void setAddress(String ipESP, int port) {
        this.ipESP = ipESP;
        this.port = port;
    }

    public ESPSerial(String ipESP, int port, int maxStringBuffer) {
        setAddress(ipESP,port);
        buffer = new byte[maxStringBuffer];
    }

    public ESPSerial(String ipESP, int port) {
        this(ipESP, port, 64*1024);
    }
}
