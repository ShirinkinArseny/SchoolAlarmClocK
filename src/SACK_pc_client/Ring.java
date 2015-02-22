package SACK_pc_client;

public class Ring {

    private int time;

    public Ring(String time) throws IllegalTimeFormatException {
        String[] parts=time.split("[: .,/]");
        if (parts.length!=3) {
            throw new IllegalTimeFormatException("Wrong params num, expected 3, got "+parts.length);
        }

        try {
            this.time=3600*Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new IllegalTimeFormatException("Can't parse firs arg: "+parts[0]);
        }


        try {
            this.time+=60*Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalTimeFormatException("Can't parse firs arg: "+parts[0]);
        }


        try {
            this.time+=Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalTimeFormatException("Can't parse firs arg: "+parts[0]);
        }

        this.time/=10;
        this.time*=10;
    }

    public float getHours() {
        return time/3600f;
    }

    public String getHumanTime() {
        String h= String.valueOf(time/3600);
        String m=String.valueOf(time/60%60);
        String s=String.valueOf(time%60);
        if (m.length()==1) m="0"+m;
        if (s.length()==1) s="0"+s;
        return h+":"+m+":"+s;
    }

}
