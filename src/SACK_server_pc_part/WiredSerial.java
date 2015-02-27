package SACK_server_pc_part;

import jssc.SerialPort;
import jssc.SerialPortException;

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
        sendByte((byte)'\n');
    }

    @Override
    String readString() throws Exception {
        return serialPort.readString();
    }

    private boolean tryWithIndex(int index) {
        try {
            serialPort = new SerialPort("/dev/ttyUSB"+index);
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            Logger.logInfo("Serial", "Connected to serial on /dev/ttyUSB"+index);
            return true;
        } catch (SerialPortException e) {
            Logger.logError("Serial", "Can't connect to serial on /dev/ttyUSB"+index+", cuz "+e.toString());
            return false;
        }
    }

    public WiredSerial() {
        for (int i=0; i<1000; i++) {
            if (tryWithIndex(i)) return;
        }
    }
}
