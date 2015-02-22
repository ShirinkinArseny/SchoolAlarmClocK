package SACK_pc_client;

import java.util.ArrayList;

public class DataWrapper {

    private static ArrayList<Ring> rings=getDefaultRings();

    private static ArrayList<Ring> getDefaultRings() {
        ArrayList<Ring> res = new ArrayList<>();
        try {
            res.add(new Ring("9:00:00"));
            res.add(new Ring("9:45:00"));
            res.add(new Ring("9:55:00"));
            res.add(new Ring("10:40:00"));
            res.add(new Ring("10:50:00"));
            res.add(new Ring("11:35:00"));
            res.add(new Ring("11:55:00"));
            res.add(new Ring("12:40:00"));
            res.add(new Ring("12:55:00"));
            res.add(new Ring("13:40:00"));
            res.add(new Ring("13:50:00"));
            res.add(new Ring("14:35:00"));
            res.add(new Ring("14:45:00"));
            res.add(new Ring("15:30:00"));

        } catch (IllegalTimeFormatException e) {
            e.printStackTrace();
        }
        return res;
    }



    public static ArrayList<Ring> getRings(int weekDay) {
        return rings;
    }

}
