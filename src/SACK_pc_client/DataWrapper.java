package SACK_pc_client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class DataWrapper {

    private static ArrayList<Ring> rings=getDefaultRings();
    private static ArrayList<Ring>[] weekdays;


    private static ArrayList<Ring> getDefaultRings() {

        Random rnd=new Random();

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
            res.add(new Ring("15:40:00"));
            res.add(new Ring("16:25:00"));
            res.add(new Ring("16:35:00"));
            res.add(new Ring("17:20:00"));
            res.add(new Ring("17:30:00"));
            res.add(new Ring("18:15:00"));

        } catch (IllegalTimeFormatException e) {
            e.printStackTrace();
        }

        weekdays=new ArrayList[7];
        for (int i=0; i<7; i++) {
            weekdays[i]=new ArrayList<>();
            for (Ring r: res)
                if (rnd.nextInt(100)>30)
                    weekdays[i].add(r);
        }

        return res;
    }



    public static ArrayList<Ring> getRings(int weekDay) {
        return weekdays[weekDay];
    }

    private static void sortDay(int day) {
        weekdays[day].sort((o1, o2) -> Integer.compare(o1.getSeconds(), o2.getSeconds()));
    }

    public static void setRings(int weekday, ArrayList<Ring> rings) {
        weekdays[weekday]=rings;
        sortDay(weekday);
    }

    public static void addRings(int weekday, ArrayList<Ring> rings) {
        weekdays[weekday].addAll(rings);
        sortDay(weekday);
    }

    public static void addRing(int weekday, Ring r) {
        weekdays[weekday].add(r);
        sortDay(weekday);
    }

    public static void pop() {

    }

    public static void push() {

    }
}
