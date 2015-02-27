package PC_part.ComPort;

import PC_part.SACK_server_pc_part.Logger;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class WiredSerial extends Serial {
    private static SerialPort serialPort;

    @Override
    void initConnection() {
    }

    @Override
    void closeConnection() {
    }

    @Override
    void sendByte(byte b) {
        try {
            serialPort.writeByte(b);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Override
    void sendString(String s) {
        try {
            serialPort.writeString(s);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    String readString() {
            try {
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
