package PC_part.J35DB_pc_part;

public class Lang {

    private static final int a=0b10001001;
    private static final int b =0b10001011;
    private static final int c =0b10001101;
    private static final int d =0b10001111;
    private static final int e =0b10010001;
    private static final int f =0b10010011;
    private static final int g =0b10010101;
    private static final int h =0b10010111;
    private static final int i =0b10011001;
    private static final int j =0b10011011;
    private static final int k =0b10011101;
    private static final int l =0b10011111;
    private static final int m =0b10100011;
    private static final int n =0b10100101;
    private static final int o =0b10100111;
    private static final int p =0b10101001;
    private static final int q =0b10101011;
    private static final int r =0b10101101;
    private static final int s =0b10101111;
    private static final int t =0b10110001;
    private static final int u =0b10110011;
    private static final int v =0b10110101;
    private static final int w =0b10110111;
    private static final int x =0b10111001;
    private static final int y =0b10111011;
    private static final int z =0b10111101;
    private static final int COMMA =0b10111111;
    private static final int OPEN_BRACKET =0b11000101;
    private static final int DOT =0b11000111;
    private static final int PLUS =0b11001001;
    private static final int SPACE =0b11001011;
    private static final int MORE =0b11001101;
    private static final int DOLLAR =0b11001111;
    private static final int DOUBLEDOT =0b11010001;
    private static final int DIVIDE =0b11010011;
    private static final int NUM_1 =0b11010101;
    private static final int NUM_3=0b11010111;
    private static final int NUM_5=0b11011001;
    private static final int NUM_7=0b11011011;
    private static final int NUM_9=0b11011101;
    private static final int CLOSE_BRACKET=0b11011111;
    private static final int MINUS=0b11100011;
    private static final int EQUALS=0b11100101;
    private static final int LESS=0b11100111;
    private static final int PERCENT=0b11101001;
    private static final int MULTIPLY=0b11101011;
    private static final int NUM_0=0b11101101;
    private static final int NUM_2=0b11101111;
    private static final int NUM_4=0b11110001;
    private static final int NUM_6=0b11110011;
    private static final int NUM_8=0b11110101;
    private static final int NEXTLINE=0b11110111;
    private static final int ASK=0b11111001;
    private static final int LOUD=0b11111011;
    private static final int NUM=0b11111101;
    private static final int DOGE=0b11111111;

    private static final int[] values = {a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, OPEN_BRACKET, CLOSE_BRACKET, DOT, COMMA, MINUS, PLUS, EQUALS, SPACE, LESS, MORE, PERCENT, DOLLAR, DOUBLEDOT, MULTIPLY, DIVIDE, NUM_0,
            NUM_1,
            NUM_2,
            NUM_3,
            NUM_4,
            NUM_5,
            NUM_6,
            NUM_7,
            NUM_8,
            NUM_9,
            NEXTLINE, ASK,
            LOUD,
            NUM,
            DOGE};
    private static final String[] representation = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "(", ")", ".", ",", "-", "+", "=", " ", "<", ">", "%", "$", ":", "*", "/",
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "\n", "?", "!", "№", "@"
    };

    public static String parseByte(int a) {
        for (int i=0; i<values.length; i++) {
            if (values[i]==a)
                return representation[i];
        }
        return null;
    }

    private static int vars = 0;

    private static void genLetter(int[] a, int pos, int length) {

        if (pos == length) {

            for (int i = 0; i < length; i++)
                System.out.print(a[i]);
            System.out.println();
            vars++;
        } else {

            if (pos < 3) {
                a[pos] = 0;
                genLetter(a, pos + 1, length);
                a[pos] = 1;
                genLetter(a, pos + 1, length);
            } else {
                if (!(a[pos - 1] == 0 && a[pos - 2] == 0 && a[pos - 3] == 0)) {
                    a[pos] = 0;
                    genLetter(a, pos + 1, length);
                }

                a[pos] = 1;
                genLetter(a, pos + 1, length);
            }
        }

    }

    public static void main(String[] args) {
        int length=8;
        int[] data=new int[8]; data[0]=1;
        genLetter(data, 1, length-1);
        System.out.println("Variants: "+vars);
    }

}
