package PC_part.SACK_server_pc_part;

import PC_part.Common.Logger;
import PC_part.Common.Serial.Serial;
import PC_part.Common.Serial.WiredSerial;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Objects;

import static PC_part.SACK_server_pc_part.ContentUtils.loadLocal;
import static PC_part.Common.Logger.logError;
import static PC_part.Common.Logger.logInfo;
import static PC_part.Common.Logger.logWarning;

public class Main {


    private static final String correctHTTPResponse200 = "HTTP/1.1 200 OK\n" +
            "Content-Type: text/html\n" +
            "Connection: close"+loadLocal("Common.html");

    private static final String correctHTTPResponse404 = "HTTP/1.1 404 Not Found\n" +
            "Content-Type: text/html\n" +
            "Connection: close"+loadLocal("404.html");


    private static final Serial serial=new WiredSerial();

    private static final String css = loadLocal("Style.css");
    private static final String js = loadLocal("Scripts.js");

    public static void main(String[] args) throws Throwable {

        serial.connect(WiredSerial.getPorts()[0]);

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
                Logger.logInfo("Main", "Requested " + address);

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
                                serial.talkWithDuino(Serial.Action.SetRings, s.toString());
                                os.write("OK".getBytes());
                                break;
                            case "time":
                                os.write("OK".getBytes());
                                serial.talkWithDuino(Serial.Action.SetTime, s.toString());
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
                            os.write(serial.talkWithDuino(Serial.Action.RequestRings, null).getBytes());
                            break;
                        case "getTime":
                            os.write(serial.talkWithDuino(Serial.Action.RequestTime, null).getBytes());
                            break;
                        case "getWeekDay":
                            os.write(serial.talkWithDuino(Serial.Action.RequestWeekDay, null).getBytes());
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