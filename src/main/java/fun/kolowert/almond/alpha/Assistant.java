package fun.kolowert.almond.alpha;

import java.util.ArrayList;
import java.util.List;

import fun.kolowert.almond.serv.Serv;
import fun.kolowert.almond.type.GameType;

public class Assistant {

    private Assistant() {}



    public static void displayTab(List<int[]> tab) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        for (int[] line : tab) {
            StringBuilder h = new StringBuilder(Serv.normIntX(++counter, 3, "0") + " | ");
            for (int i = 0; i < line.length; i++) {
                h.append(" ").append(Serv.normIntX(line[i], 3, " ")).append("  : ");
            }
            result.append(h).append(System.lineSeparator());
        }
        System.out.println(Serv.cut(result.toString(), 1));
    }

    public static String displayPlainHead(GameType gameType, String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 1; i <= gameType.getGameSetSize(); i++) {
            sb.append(Serv.normIntX(i, 2, "0")).append("|");
        }
        return sb.toString();
    }

    public static String displayPlainHead(GameType gameType, String prefix, String indent) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 1; i <= gameType.getGameSetSize(); i++) {
            sb.append(indent).append(Serv.normIntX(i, 2, " ")).append(" |");
        }
        return sb.toString();
    }

    public static void displayRangesHead(int[] mask) {
        StringBuilder sb = new StringBuilder();
        int left = 1;
        for (int i = 0; i < mask.length; i++) {
            sb.append(Serv.normIntX(left, 2, "0")).append("-").append(Serv.normIntX(mask[i], 2, "0")).append(" | ");
            left = mask[i] + 1;
        }
        System.out.println("mask| " + sb);
    }

    public static void displayHitsOnRangesResume(List<int[]> inputTab, int[] mask) {
        // Sum
        int[] sumLine = new int[mask.length];
        for (int[] line : inputTab) {
            for (int i = 0; i < line.length && i < sumLine.length; i++) {
                sumLine[i] += line[i];
            }
        }
        List<int[]> wrapingList = new ArrayList<>();
        wrapingList.add(sumLine);
        displayTab(wrapingList);

        // Coefficient
        double[] coef = new double[mask.length];
        int size = inputTab.size();
        for (int i = 0; i < sumLine.length && i < mask.length; i++) {
            coef[i] = 1.0 * sumLine[i] / size;
        }
        // Display Coefficient line
        StringBuilder sb = new StringBuilder("coef| ");
        for (int i = 0; i < coef.length; i++) {
            sb.append(Serv.normDoubleX(coef[i], 3)).append(" | ");
        }
        System.out.println(sb);
    }
}
