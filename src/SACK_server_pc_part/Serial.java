package SACK_server_pc_part;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.ArrayList;

public class Serial {

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

    public Serial() {
        for (int i=0; i<1000; i++) {
            if (tryWithIndex(i)) return;
        }
    }
}