package SACK_server_pc_part;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static SACK_server_pc_part.ContentUtils.loadLocal;
import static SACK_server_pc_part.Logger.logError;
import static SACK_server_pc_part.Logger.logInfo;
import static SACK_server_pc_part.Logger.logWarning;

public class Main {


    public static final String correctHTTPResponse200 = "HTTP/1.1 200 OK\n" +
            "Content-Type: text/html\n" +
            "Connection: close"+loadLocal("Common.html");

    private static final String correctHTTPResponse404 = "HTTP/1.1 404 Not Found\n" +
            "Content-Type: text/html\n" +
            "Connection: close"+loadLocal("404.html");


    private static final Serial serial=new Serial();

    public static final String css = loadLocal("Style.css");
    public static final String js = loadLocal("Scripts.js");

    public static void main(String[] args) throws Throwable {

        int port = -1;
        if (args.length == 0) {
            port = 80;
            logWarning("main", "No port specified, using default: " + port);
        } else {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                logError("main", "Failed to parse port: " + args[0]);
                System.exit(1);
            }
        }


        logInfo("main", "Binding port: " + port);
        ServerSocket ss = new ServerSocket(port);
        boolean isWorking = true;
        while (isWorking) {
            Socket s = ss.accept();
            new Thread(new SocketProcessor(s)).start();
        }
    }

    public enum Action {RequestWeekDay, RequestTime, RequestRings, SetRings, SetTime}
    private synchronized static String talkWithDuino(Action act, String s) {
        switch (act) {
            case RequestWeekDay: return serial.request("5");
            case RequestTime: return serial.request("2");
            case RequestRings: return serial.request("1");
            case SetRings: {
                Logger.logInfo("DUINOTALK", "Printing table to EEPROM! input: " + s);
                ArrayList<Short> bytes=new ArrayList<>();
                bytes.add((short) '3');
                String[] times=s.split(":");
                System.out.println(Arrays.toString(times));
                for (int day=0; day<7; day++) {
                    if (times[day].length()==0) {
                        Logger.logInfo("DUINOTALK", "11day: " + day);
                        bytes.add((short) 0);
                    }
                    else {
                        Logger.logInfo("DUINOTALK", "22day: " + day);
                        String[] rings = times[day].split(",");
                        bytes.add((short) (rings.length-1));
                        for (int j=1; j<rings.length; j++) {
                            int value=Integer.valueOf(rings[j]);
                            bytes.add((short) (value / 256));
                            bytes.add((short) (value % 256));
                        }
                    }
                }
                Logger.logInfo("DUINOTALK", "22Printing table to EEPROM! input: " + s);
                String resp=serial.request(bytes);
                Logger.logInfo("DUINOTALK", "Printing table to EEPROM! answer: " + resp + " input: " + s);
                return resp;
            }
            case SetTime: {

                String[] splitted=s.split(":");
                if (splitted.length==6) {
                    int hour=Integer.valueOf(splitted[0]);
                    int minute=Integer.valueOf(splitted[1]);
                    int second=Integer.valueOf(splitted[2]);
                    int day=Integer.valueOf(splitted[3]);
                    int month=Integer.valueOf(splitted[4]);
                    int year=Integer.valueOf(splitted[5]);

                    if
                            (hour>=0 && hour<24 &&
                            minute>=0 && minute<60 &&
                            second>=0 && second<60 &&
                            day>=1 && day<=31 &&
                            month>=1 && month<=12 &&
                            year>=1970 && year<2030
                            ) {


                        ArrayList<Short> bytes=new ArrayList<>();
                        bytes.add((short) '6');
                        bytes.add((short) (hour%256));
                        bytes.add((short) (minute%256));
                        bytes.add((short) (second%256));
                        bytes.add((short) (day%256));
                        bytes.add((short) (month%256));
                        bytes.add((short) (year/256));
                        bytes.add((short) (year%256));
                        Logger.logInfo("DUINOTALK", "Input: " + s);
                        Logger.logInfo("DUINOTALK", "Sent: " + bytes);
                        String resp=serial.request(bytes);
                        Logger.logInfo("DUINOTALK", "Responsed: " + resp);
                        return resp;
                    }
                    Logger.logError("DUINOTALK", "Wrong time string: "+ Arrays.toString(splitted) +" - values are not in bounds");
                }
                Logger.logError("DUINOTALK", "Wrong time string: "+ Arrays.toString(splitted) +" - not 6 args");
                return null;
            }
        }return null;
    }

    private static class SocketProcessor implements Runnable {

        private final Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        public void run() {
            try {
                Charset streamCharset = Charset.forName("UTF-8");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is, streamCharset));

                String line = in.readLine();
                if (line==null) {
                    throw new Exception("Null input line");
                }

                String firstLine = line.substring(0, line.indexOf(" HTTP"));
                String[] words = firstLine.split(" ");
                String address = words[1];
                if (address.startsWith("/")) address = address.substring(1);
                Logger.logInfo("Main", "Requested "+ address);

                if (line.startsWith("POST")) {//processing post-request

                    boolean contentTypeDefined = false;
                    boolean contentLengthDefined = false;

                    int contentLength = 0;

                    String type = null;

                    //defining contentLength and contentType
                    while (!contentTypeDefined || !contentLengthDefined) {
                        line = in.readLine();

                        if (line.contains("Content-Type:")) {
                            type = line.split(";")[0].split(" ")[1];
                            contentTypeDefined = true;
                        }

                        if (line.contains("Content-Length")) {
                            contentLength = Integer.parseInt(line.split(" ")[1]);
                            contentLengthDefined = true;
                        }

                    }

                    //processing simple forms (NOT BIG-CONTENT FORMS!)
                    if (Objects.equals(type, "application/x-www-form-urlencoded")) {

                        while (!line.equals("")) {
                            line = in.readLine();
                        }
                        StringBuilder s = new StringBuilder(contentLength);
                        int cLength = 0;
                        while (cLength < contentLength) {
                            String c = String.valueOf((char) in.read());
                            cLength += c.getBytes().length;
                            s = s.append(c);
                        }

                        switch(address) {
                            case "table":
                                talkWithDuino(Action.SetRings, s.toString());
                                os.write("OK".getBytes());
                                break;
                            case "time":
                                os.write("OK".getBytes());
                                talkWithDuino(Action.SetTime, s.toString());
                                break;
                        }

                    } else {
                        logWarning("POST", "Unknown content type: " + type);
                    }
                } else

                //processing GET-request
                if (line.startsWith("GET")) {

                    switch (address) {
                        case "Scripts.js":
                            os.write(js.getBytes());
                            break;
                        case "Style.css":
                            os.write(css.getBytes());
                            break;
                        case "getRings":
                            os.write((talkWithDuino(Action.RequestRings, null)).getBytes());
                            break;
                        case "getTime":
                            os.write(talkWithDuino(Action.RequestTime, null).getBytes());
                            break;
                        case "getWeekDay":
                            os.write(talkWithDuino(Action.RequestWeekDay, null).getBytes());
                            break;
                        case "":
                            os.write(correctHTTPResponse200.getBytes());
                            break;
                        default:
                            os.write(correctHTTPResponse404.getBytes());
                            break;
                    }


                } else
                //unknown request
                {
                    os.write(correctHTTPResponse404.getBytes());
                    throw new Exception();
                }


            } catch (Exception e) {
                //e.printStackTrace();
            } finally {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}