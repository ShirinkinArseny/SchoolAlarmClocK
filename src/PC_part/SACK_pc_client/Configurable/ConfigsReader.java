package PC_part.SACK_pc_client.Configurable;

import PC_part.SACK_pc_client.DataWrapper;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConfigsReader {

    private static ScriptEngine engine;
    private static boolean loaded=false;

    private static String readConfigs() throws Exception {
        StringBuilder lines=new StringBuilder();
        BufferedReader input;
        input = new BufferedReader(new InputStreamReader(ConfigsReader.class.getResourceAsStream("configs.js")));
        String line;
        while ((line = input.readLine()) != null) {
            lines.append(line);
            lines.append('\n');
        }
        input.close();
        return String.valueOf(lines);
    }

    public static float getFloatValue(String name) {

        if (!loaded)
            init();

        try {
            return Float.valueOf(engine.eval(name).toString());
        } catch (ScriptException e) {
            DataWrapper.processError("Не могу прочитать параметр из конфига: "+
                    name+", потому что "+e.getMessage());
            System.exit(1);
            return -1;
        }
    }

    public static int getIntValue(String name) {

        if (!loaded)
            init();

        try {
            return Integer.valueOf(engine.eval(name).toString());
        } catch (ScriptException e) {
            DataWrapper.processError("Не могу прочитать параметр из конфига: "+
                    name+", потому что "+e.getMessage());
            System.exit(1);
            return -1;
        }
    }

    public static String getStringValue(String name) {

        if (!loaded)
            init();

        try {
            return String.valueOf(engine.eval(name));
        } catch (ScriptException e) {
            DataWrapper.processError("Не могу прочитать параметр из конфига: " +
                    name + ", потому что " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    public static void init() {
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
        String configsText="";
        try {
            configsText=readConfigs();
        } catch (Exception e) {
            DataWrapper.processError("Не могу прочитать конфиги :c");
            System.exit(1);
        }
        try {
            engine.eval(configsText);
        } catch (ScriptException e) {
            DataWrapper.processError("Кривые конфиги: "+e.getMessage());
            System.exit(1);
        }

        loaded=true;
    }

}
