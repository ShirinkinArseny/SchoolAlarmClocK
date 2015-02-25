package PC_part.SACK_server_pc_part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ContentUtils {

    /**
     * Reads the file in current jar-archive (or classpath)
     * Slower than second method, but useful to postprocess string
     *
     * @param filename Name of file (in same directory with this class)
     * @return file containment
     */
    public static String loadLocal(String filename) {
        String code = "";
        String line;

        InputStream in = ContentUtils.class.getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = reader.readLine()) != null) {
                code += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * Reads the file in current jar-archive (or classpath)
     * Faster than reading string
     *
     * @param f Name of file (in same directory with this class)
     * @return file containment as byte array
     */
    public static byte[] readFileByteArray(String f) {
        InputStream in = ContentUtils.class.getResourceAsStream(f);
        ArrayList<Byte> bytes = new ArrayList<>();
        byte[] c = new byte[1024];
        int count;
        try {
            while ((count = in.read(c)) != -1) {
                for (int i = 0; i < count; i++)
                    bytes.add(c[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            byteArray[i] = bytes.get(i);
        return byteArray;
    }

}