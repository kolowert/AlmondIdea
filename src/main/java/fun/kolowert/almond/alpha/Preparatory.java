package fun.kolowert.almond.alpha;

import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.Serv;
import fun.kolowert.almond.type.GameType;

import java.util.ArrayList;
import java.util.List;

/**
 * Prepare ResultSet for string-reports
 */
public class Preparatory {

    private Preparatory() {}

    public static String reportIntArray(int[] input) {
        StringBuilder sb = new StringBuilder(2 + 3 * input.length);
        sb.append("[");
        for (int d : input) {
            sb.append(Serv.normIntX(d, 2, " "));
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1) + "]";
    }

    public static String reportDoubleArray(double[] input) {
        StringBuilder sb = new StringBuilder(2 + 6 * input.length);
        sb.append("[");
        for (double d : input) {
            sb.append(Serv.normDoubleX(d, 1, 2));
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1) + "]";
    }

    protected static String prepareFrequenciesTab(ParamSet paramSet, ResultSet resultSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nFrequencies Tab").append(System.lineSeparator());

        List<double[]> frequenciesTab = resultSet.getFrequenciesTab();
        StringBuilder result = new StringBuilder();
        for (double[] frequencies : frequenciesTab) {
            StringBuilder line = new StringBuilder(6 * frequencies.length);
            for (int i = frequencies.length - 1; i >= 0; i--) {
                int order = (int) frequencies[i];
                int preball = (int) (100 * (frequencies[i] - order) + 0.005);
                String ball = Serv.normIntX(preball, 2, "0");
                line.append(Serv.normIntX(order, 5, " ")).append("(").append(ball).append(")").append(" ");
            }
            result.append(line).append(System.lineSeparator());
        }

        sb.append(reportPlainHead(paramSet.gameType, "", "      "));
        sb.append(System.lineSeparator());
        sb.append(result.substring(0, result.length() - 2));

        return sb.toString();
    }

    protected static String prepareHitsOnFrequenciesTab(ResultSet resultSet) {
        StringBuilder sb = new StringBuilder("\nHits On Frequencies Tab\n");

        int[] mask = resultSet.getHitRangesMask();
        // prepare head
        int len = resultSet.getHistOrderResultTab().get(0).length;
        int[] heads = new int[len];
        for (int i = 0; i < len; i++) { heads[i] = i + 1; }
        int[] order = new int[len];
        String headLine = reportHitsOnFrequenciesLine(">>>>> |", heads, order, mask);
        sb.append(headLine).append(System.lineSeparator());

        List<double[]> rawFrequenciesTab = resultSet.getFrequenciesTab();
        List<int[]> ballsOnFrequenciesTab = extractBallsOrder(rawFrequenciesTab);
        List<int[]> histOrderResultTab = resultSet.getHistOrderResultTab();

        int counter = histOrderResultTab.size() - 1;

        for (int index = 0; index < ballsOnFrequenciesTab.size(); index++) {
            String prefix = Serv.normIntX(resultSet.getId() - counter--, 5, "0") + " |";
            int[] balls = ballsOnFrequenciesTab.get(index);
            int[] hitsOrder = histOrderResultTab.get(index);
            String lineReport = reportHitsOnFrequenciesLine(prefix, balls, hitsOrder, mask);
            sb.append(lineReport).append(System.lineSeparator());
        }

        return Serv.cut(sb.toString(), 1);
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

    private static boolean isInArray(int n, int[] arr) {
        for (int a : arr) {
            if (a == n) {
                return true;
            }
        }
        return false;
    }

    protected static String prepareHistOrderResultTab(ParamSet paramSet, ResultSet resultSet) {
        StringBuilder sb = new StringBuilder("\nHist Order Result Tab\n");

        sb.append(reportPlainHead(paramSet.gameType, paramSet.displayPrefix)).append(System.lineSeparator());
        List<int[]> histOrderResultTab = resultSet.getHistOrderResultTab();
        int counter = histOrderResultTab.size() - 1;
        for (int[] hits : histOrderResultTab) {
            sb.append(reportIntArray(hits, paramSet.hitRangesMask,
                    Serv.normIntX(resultSet.getId() - counter--, 5, "0") + " |"));
            sb.append(System.lineSeparator());
        }
        int[] histOrderResultTabSum = new int[histOrderResultTab.get(0).length];
        for (int[] hits : histOrderResultTab) {
            for (int i = 0; i < hits.length; i++) {
                histOrderResultTabSum[i] += hits[i];
            }
        }
        sb.append("\nSum of histOrderResultTab").append(System.lineSeparator());
        sb.append(reportPlainHead(paramSet.gameType, paramSet.displayPrefix));
        sb.append(System.lineSeparator());
        sb.append(reportIntArray(histOrderResultTabSum, paramSet.hitRangesMask, paramSet.displayPrefix));
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    private static String reportPlainHead(GameType gameType, String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 1; i <= gameType.getGameSetSize(); i++) {
            sb.append(Serv.normIntX(i, 2, "0")).append("|");
        }
        return sb.toString();
    }

    private static String reportPlainHead(GameType gameType, String prefix, String indent) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 1; i <= gameType.getGameSetSize(); i++) {
            sb.append(indent).append(Serv.normIntX(i, 2, " ")).append(" |");
        }
        return sb.toString();
    }

    private static String reportIntArray(int[] input, int[] rangesMask, String prefix) {
        return reportIntArray(input, rangesMask, prefix, 2, " ", ":");
    }

    private static String reportIntArray(int[] input, int[] rangesMask, String prefix, int fieldSize, String placeHolder,
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

    protected static String prepareHitsOnRanges(ResultSet resultSet) {
        StringBuilder sb = new StringBuilder("Hits On Ranges\n");

        sb.append(reportRangesHead(resultSet.getHitRangesMask()));
        sb.append(System.lineSeparator());
        sb.append(reportTab(resultSet.getHitsOnRanges()));
        sb.append(System.lineSeparator());
        sb.append(reportHitsOnRangesResume(resultSet));
        return sb.toString();
    }

    private static String reportRangesHead(int[] mask) {
        StringBuilder sb = new StringBuilder("mask| ");
        int left = 1;
        for (int i = 0; i < mask.length; i++) {
            sb.append(Serv.normIntX(left, 2, "0")).append("-")
                    .append(Serv.normIntX(mask[i], 2, "0")).append(" | ");
            left = mask[i] + 1;
        }
        return  sb.toString();
    }

    public static String reportTab(List<int[]> tab) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        for (int[] line : tab) {
            StringBuilder h = new StringBuilder(Serv.normIntX(++counter, 3, "0") + " | ");
            for (int i = 0; i < line.length; i++) {
                h.append(" ").append(Serv.normIntX(line[i], 3, " ")).append("  : ");
            }
            result.append(h).append(System.lineSeparator());
        }
        return Serv.cut(result.toString(), 1);
    }

    private static String reportHitsOnRangesResume(ResultSet resultSet) {
        // report Sum line
        StringBuilder result = new StringBuilder("Sum\n");
        int[] sumLine = resultSet.getHitsOnRangesSum();
        List<int[]> wrappingList = new ArrayList<>();
        wrappingList.add(sumLine);
        result.append(reportTab(wrappingList));
        result.append(System.lineSeparator());

        // report Coefficient line
        double[] hitsOnRangesAvg = resultSet.getAvgHitsOnRanges();
        StringBuilder sb = new StringBuilder("avg | ");
        for (int i = 0; i < hitsOnRangesAvg.length; i++) {
            sb.append(Serv.normDoubleX(hitsOnRangesAvg[i], 3)).append(" | ");
        }
        result.append(sb);
        return result.toString();
    }

}
