package PC_part.SACK_pc_client;

import java.util.ArrayList;

public class PlayingAroundBytes {

    public static byte intToByte(int i) {
        return (byte) i;
    }

    public static int byteToInt(byte i) {
        if (i<0) return 256+i;
        return i;
    }

    public static byte[] intsToBytes(int[] ints) {
        byte[] b=new byte[ints.length];
        for (int i=0; i<ints.length; i++)
            b[i]=intToByte(ints[i]);
        return b;
    }

    private static int[] listToArray(ArrayList<Integer> ints) {
        int[] data=new int[ints.size()];
        for (int i=0; i<ints.size(); i++)
            data[i]=ints.get(i);
        return data;
    }

    public static byte[] intsToBytes(ArrayList<Integer> ints) {
        return intsToBytes(listToArray(ints));
    }

    public static int[] bytesToInts(byte[] bytes) {
        int[] b=new int[bytes.length];
        for (int i=0; i<bytes.length; i++)
            b[i]=byteToInt(bytes[i]);
        return b;
    }

}
