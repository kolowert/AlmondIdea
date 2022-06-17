package fun.kolowert.almond.alpha;

import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.Serv;
import fun.kolowert.almond.type.GameType;

import java.util.ArrayList;
import java.util.List;

public class Display {

    private Display() {
    }

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
            sb.append(Serv.normIntX(left, 2, "0")).append("-")
                    .append(Serv.normIntX(mask[i], 2, "0")).append(" | ");
            left = mask[i] + 1;
        }
        System.out.println("mask| " + sb);
    }

    public static void displayHitsOnRangesResume(ResultSet resultSet) {
        // Display sum line
        int[] sumLine = resultSet.getHitsOnRangesSum();
        List<int[]> wrappingList = new ArrayList<>();
        wrappingList.add(sumLine);
        displayTab(wrappingList);

        // Display Coefficient line
        double[] hitsOnRangesAvg = resultSet.getAvgHitsOnRanges();
        StringBuilder sb = new StringBuilder("avg | ");
        for (int i = 0; i < hitsOnRangesAvg.length; i++) {
            sb.append(Serv.normDoubleX(hitsOnRangesAvg[i], 3)).append(" | ");
        }
        System.out.println(sb);
    }

    public static void displayResultSet(ParamSet paramSet, ResultSet resultSet) {
        displayFrequenciesTab(paramSet, resultSet);
        displayHitsOnFrequenciesTab(resultSet);
        displayHistOrderResultTab(paramSet, resultSet);
        displayOnRangesReport(paramSet, resultSet);
    }

    private static void displayHitsOnFrequenciesTab(ResultSet resultSet) {
        int[] mask = resultSet.getHitRangesMask();
        // prepare & display head
        System.out.println("\nHits On Frequencies Tab");
        int len = resultSet.getHistOrderResultTab().get(0).length;
        int[] heads = new int[len];
        for (int i = 0; i < len; i++) { heads[i] = i + 1; }
        int[] order = new int[len];
        String headLine = reportHitsOnFrequenciesLine(">>>>> |", heads, order, mask);
        System.out.println(headLine);

        List<double[]> rawFrequenciesTab = resultSet.getFrequenciesTab();
        List<int[]> ballsOnFrequenciesTab = extractBallsOrder(rawFrequenciesTab);
        List<int[]> histOrderResultTab = resultSet.getHistOrderResultTab();

        int counter = histOrderResultTab.size() - 1;

        for (int index = 0; index < ballsOnFrequenciesTab.size(); index++) {
            String prefix = Serv.normIntX(resultSet.getId() - counter--, 5, "0") + " |";
            int[] balls = ballsOnFrequenciesTab.get(index);
            int[] hitsOrder = histOrderResultTab.get(index);
            String lineReport = reportHitsOnFrequenciesLine(prefix, balls, hitsOrder, mask);
            System.out.println(lineReport);
        }
    }

    private static String reportHitsOnFrequenciesLine(String prefix, int[] balls, int[] hits, int[] mask) {
        StringBuilder sb = new StringBuilder(prefix);
        String currentSeparator;
        for (int i = 0; i < balls.length; i++) {
            if (isInArray(i + 1, mask)) {
                currentSeparator = "!";
            } else {
                currentSeparator = " ";
            }
            String ballReport = Serv.normIntX(balls[i], 2, "0");
            if (hits[i] > 0) {
                sb.append("(").append(ballReport).append(")").append(currentSeparator);
            } else {
                sb.append(" ").append(ballReport).append(" ").append(currentSeparator);
            }
        }
        return sb.toString();
    }

    private static List<int[]> extractBallsOrder(List<double[]> rawFrequenciesTab) {
        List<int[]> resultTab = new ArrayList<>();
        for (double[] line : rawFrequenciesTab) {
            int[] resultLine = new int[line.length - 1];
            for (int i = 0, j = line.length - 1; i < line.length - 1 && j >= 0; i++, j--) {
                resultLine[i] = (int) (100.0 * (line[j] - (int) line[j]) + 0.5);
            }
            resultTab.add(resultLine);
        }
        return resultTab;
    }

    private static void displayFrequenciesTab(ParamSet paramSet, ResultSet resultSet) {
        List<double[]> frequenciesTab = resultSet.getFrequenciesTab();
        StringBuilder result = new StringBuilder();
        for (double[] frequencies : frequenciesTab) {
            StringBuilder sb = new StringBuilder(6 * frequencies.length);
            for (int i = frequencies.length - 1; i >= 0; i--) {
                int order = (int) frequencies[i];
                int preball = (int) (100 * (frequencies[i] - order) + 0.005);
                String ball = Serv.normIntX(preball, 2, "0");
                sb.append(Serv.normIntX(order, 5, " ")).append("(").append(ball).append(")").append(" ");
            }
            result.append(sb).append(System.lineSeparator());
        }
        System.out.println("\nFrequencies Tab");
        System.out.println(Display.displayPlainHead(paramSet.gameType, "", "      "));
        System.out.println(result.substring(0, result.length() - 2));
    }

    private static void displayHistOrderResultTab(ParamSet paramSet, ResultSet resultSet) {
        System.out.println("\nhistOrderResultTab");
        System.out.println(Display.displayPlainHead(paramSet.gameType, paramSet.displayPrefix));
        List<int[]> histOrderResultTab = resultSet.getHistOrderResultTab();
        int counter = histOrderResultTab.size() - 1;
        for (int[] hits : histOrderResultTab) {
            System.out.println(reportIntArray(hits, paramSet.hitRangesMask,
                    Serv.normIntX(resultSet.getId() - counter--, 5, "0") + " |"));
        }
        int[] histOrderResultTabSum = new int[histOrderResultTab.get(0).length];
        for (int[] hits : histOrderResultTab) {
            for (int i = 0; i < hits.length; i++) {
                histOrderResultTabSum[i] += hits[i];
            }
        }
        System.out.println("\nSum of histOrderResultTab");
        System.out.println(Display.displayPlainHead(paramSet.gameType, paramSet.displayPrefix));
        System.out.println(reportIntArray(histOrderResultTabSum, paramSet.hitRangesMask, paramSet.displayPrefix));
    }

    private static void displayOnRangesReport(ParamSet paramSet, ResultSet resultSet) {
        displayHitsOnRanges(resultSet);
        System.out.println("\nzeroesOnHitsOnRanges: " + resultSet.getZeroesOnHitsOnRanges()
                + " / " + resultSet.getHitsOnRanges().size()
                + " >> zero coefficient: # " + Serv.normDoubleX(resultSet.getZeroCoefficient(), 2)
                + " # >>" + " on params: " + paramSet);
        System.out.println("WholeLinesOnHitsOnRanges: " + resultSet.getWholeLinesOnHitsOnRanges()
                + "  wholeLinesCoefficient: " + Serv.normDoubleX(resultSet.getWholeLinesCoefficient(), 2));
    }

    private static void displayHitsOnRanges(ResultSet resultSet) {
        System.out.println("\nHits On Ranges");
        Display.displayRangesHead(resultSet.getHitRangesMask());
        Display.displayTab(resultSet.getHitsOnRanges());
        System.out.println("Sum");
        Display.displayHitsOnRangesResume(resultSet);
    }

    public static String reportIntArray(int[] input, String prefix) {
        return reportIntArray(input, prefix, 2, " ", ":", true);
    }

    public static String reportIntArray(int[] input, String prefix, int fieldSize, String placeHolder,
                                        String columnSeparator, boolean isEmptyWhenZero) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < input.length; i++) {
            if (input[i] == 0) {
                sb.append("  ").append(columnSeparator);
            } else {
                sb.append(Serv.normIntX(input[i], fieldSize, placeHolder)).append(columnSeparator);
            }
        }
        return sb.toString();
    }

    public static String reportIntArray(int[] input, int[] rangesMask, String prefix) {
        return reportIntArray(input, rangesMask, prefix, 2, " ", ":");
    }

    public static String reportIntArray(int[] input, int[] rangesMask, String prefix, int fieldSize, String placeHolder,
                                        String columnSeparator) {
        StringBuilder sb = new StringBuilder(prefix);
        String currentColumnSeparator;
        for (int i = 0; i < input.length; i++) {
            if (isInArray(i + 1, rangesMask)) {
                currentColumnSeparator = "!";
            } else {
                currentColumnSeparator = columnSeparator;
            }
            if (input[i] == 0) {
                sb.append("  ").append(currentColumnSeparator);
            } else {
                sb.append(Serv.normIntX(input[i], fieldSize, placeHolder)).append(currentColumnSeparator);
            }
        }
        return sb.toString();
    }

    // servant for reportIntArray(..)
    private static boolean isInArray(int n, int[] arr) {
        for (int a : arr) {
            if (a == n) {
                return true;
            }
        }
        return false;
    }
}
