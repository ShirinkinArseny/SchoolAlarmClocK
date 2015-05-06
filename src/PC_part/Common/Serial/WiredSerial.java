package PC_part.Common.Serial;

import PC_part.Common.Logger;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.DataWrapper;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class WiredSerial extends Serial {
    private static SerialPort serialPort;

    @Override
    public void initConnection() {
    }

    @Override
    public void closeConnection() {
    }

    @Override
    void sendBytes(byte[] b) {
        try {
            serialPort.purgePort(SerialPort.PURGE_RXCLEAR+SerialPort.PURGE_TXCLEAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        try {
            serialPort.writeBytes(b);
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
    }

    @Override
    public byte[] readBytes() {
        try {
            Thread.sleep(100);
            return serialPort.readBytes();
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
            serialPort.setParams(SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Logger.logInfo(this.getClass(), "Connected to serial on " + name);
            connected=true;
        } catch (SerialPortException e1) {
            e1.printStackTrace();
            connected=false;
        }
    }

    public void disconnect() {
        try {
            serialPort.closePort();
            Logger.logInfo(this.getClass(), "Disconnected from serial");
        } catch (SerialPortException e) {
            Logger.logError(this.getClass(), "Can't disconnect from serial, cuz " + e.toString());
            DataWrapper.processError(Labels.cannotDisconnect);
        }
        connected=false;
    }

    public static String[] getPorts() {
        return SerialPortList.getPortNames();
    }
}
