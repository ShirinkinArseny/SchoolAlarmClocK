package PC_part.ComPort;

import PC_part.SACK_server_pc_part.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class WiredSerial extends Serial {
    private static SerialPort serialPort;

    @Override
    void initConnection() throws Exception {
    }

    @Override
    void closeConnection() throws Exception {
    }

    @Override
    void sendByte(byte b) throws Exception {
        serialPort.writeByte(b);
    }
    @Override
    void sendString(String s) throws Exception {
        serialPort.writeString(s);
    }

    @Override
    void sendStop() throws Exception {
        sendByte((byte)'\r');
        sendByte((byte) '\n');
    }

    @Override
    String readString() {
            try {
                Thread.sleep(50);
                return readString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
    }

    private boolean connected=false;

    public boolean getIsConnected() {
        return connected;
    }

    public WiredSerial() {
    }

    public void connect(String name) {
        serialPort = new SerialPort(name);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Logger.logInfo("WiredSerial", "Connected to serial on " + name);
            connected=true;
        } catch (SerialPortException e1) {
            e1.printStackTrace();
            connected=false;
        }
    }

    public void disconnect() {
        try {
            serialPort.closePort();
            Logger.logInfo("WiredSerial", "Disconnected from serial");
        } catch (SerialPortException e) {
            Logger.logError("WiredSerial", "Can't disconnect from serial, cuz " + e.toString());
        }
        connected=false;
    }

    public static String[] getPorts() {
        return SerialPortList.getPortNames();
    }
}
