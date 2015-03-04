package PC_part.SACK_server_pc_part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

class ContentUtils {

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

}