package PC_part.SACK_pc_client;

public class Ring {

    private int arduinoMemoryRepresentation;

    public Ring(int memRep) {
        arduinoMemoryRepresentation =memRep;
    }

    public Ring(int time, boolean isShort) {
        arduinoMemoryRepresentation =time/10;
        if (isShort)
            arduinoMemoryRepresentation+=0b1000000000000000;
    }

    public int getArduinoMemoryRepresentation() {
        return arduinoMemoryRepresentation;
    }

    public boolean isShort() {
        return (arduinoMemoryRepresentation & 0b1000000000000000) !=0;
    }

    public int getSeconds() {
        return (arduinoMemoryRepresentation & 0b0011111111111111)*10;
    }

    public Ring(String time) throws IllegalTimeFormatException {
        String[] parts=time.split("[: .,/]");
        if (parts.length!=3) {
            throw new IllegalTimeFormatException("Wrong params num, expected 3, got "+parts.length);
        }

        try {
            this.arduinoMemoryRepresentation =360*Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new IllegalTimeFormatException("Can't parse first arg: "+parts[0]);
        }


        try {
            this.arduinoMemoryRepresentation +=6*Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalTimeFormatException("Can't parse second arg: "+parts[1]);
        }


        try {
            this.arduinoMemoryRepresentation +=Integer.parseInt(parts[2])/10;
        } catch (NumberFormatException e) {
            throw new IllegalTimeFormatException("Can't parse third arg: "+parts[2]);
        }
    }

    public float getHours() {
        return getSeconds() /3600f;
    }

    private String getHumanTime() {
        return getHumanTime(getSeconds());
    }

    public static String getHumanTime(int time) {



        String h= String.valueOf(time/3600);
        String m=String.valueOf(time/60%60);
        String s=String.valueOf(time%60);
        if (m.length()==1) m="0"+m;
        if (s.length()==1) s="0"+s;
        return h+":"+m+":"+s;
    }

    public String toString() {
        return getHumanTime()+(isShort()?" sub":"");
    }

}
