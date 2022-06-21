package fun.kolowert.almond.serv;

import java.text.DecimalFormat;

public class Serv {
    private Serv() {
    }

    public static String normalizeArray(int[] arr, String leftBracket, String rightBracket) {
        StringBuilder sb = new StringBuilder(leftBracket + " ");
        for (int n : arr) {
            if (n < 10) {
                sb.append(" ").append(n);
            } else {
                sb.append(n);
            }
            sb.append(", ");
        }
        return sb.toString().substring(0, sb.length() - 2) + " " + rightBracket;
    }

    public static String normalizeArray(int[] arr) {
        return normalizeArray(arr, "< ", " >");
    }

    /**
     * @param number to norm
     * @param length for normed result
     * @param placeHolder before number
     * @return String like "##ddd" where # - placeholder; d - digit of number
     */
    public static String normIntX(int number, int length, String placeHolder) {
        String s = String.valueOf(number);
        int y = length - s.length();
        StringBuilder sb = new StringBuilder();
        while (y > 0) {
            sb.append(placeHolder);
            --y;
        }
        sb.append(s);
        return sb.toString();
    }

    public static String normDoubleX(double d, int x) {
        // Tip is for rounding
        double tip = 0.5;
        for (int j = 1; j <= x; j++) {
            tip = tip * 0.1;
        }
        int n = (int) d;
        String sFraction = String.valueOf(d - n + tip) + "0000000000000";
        return String.valueOf(n) + sFraction.substring(1, 2 + x);
    }

    /**
     * @param d - double input
     * @param i - a size of whole fraction of input
     * @param x - a size of fractional part of input
     * @return string like '02.357' when argument is 2.3568970025
     */
    public static String normDoubleX(double d, int i, int x) {
        // Tip is for rounding
        double tip = 0.5;
        for (int j = 1; j <= x; j++) {
            tip = tip * 0.1;
        }
        int n = (int) d;
        String sFraction = String.valueOf(d - n + tip) + "0000000000000";
        return normIntX(n, i, "0") + sFraction.substring(1, 2 + x);
    }

    // debugging
    public static void main(String[] args) {
        for (double y = 3.1415; y < 100_000_000; y *= 7.37) {
            System.out.println(y + " >> " + normDoubleX(y, 4, 3));
        }
    }

    public static String cut(String input, int tail) {
        int len = input.length();
        if (tail > len + 1) { tail = len + 1; }
        return input.substring(0, len - tail - 1);
    }

}
