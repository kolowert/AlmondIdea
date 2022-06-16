package fun.kolowert.almond.alpha;

import fun.kolowert.almond.common.MatchingCalculator;
import fun.kolowert.almond.common.ScoreCounter;
import fun.kolowert.almond.data.BigCombination;
import fun.kolowert.almond.serv.Serv;
import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Processor {
    private final GameType gameType;
    private final List<int[]> histBox;
    private final int[] nextLine;

    private final List<BigCombination> bigCombinations;
    private final int combinationsAmountLimit;
    private int minimalScore;
    private int maximalScore;

    public Processor(GameType gameType, List<int[]> histBox, int[] nextLine, int combinationsAmountLimit) {
        this.gameType = gameType;
        this.histBox = histBox;
        this.nextLine = nextLine;
        this.bigCombinations = new ArrayList<>(combinationsAmountLimit + 1);
        this.combinationsAmountLimit = combinationsAmountLimit;
        minimalScore = Integer.MAX_VALUE;
        maximalScore = 0;
    }

    public static int[] extractBallSequence(double[] frequencies) {
        int[] result = new int[frequencies.length - 1];
        for (int j = 1, i = frequencies.length - 2; i >= 0; j++, i--) {
            int order = (int) frequencies[j];
            result[i] = (int) (100 * (frequencies[j] - order) + 0.005);
        }
        return result;
    }

    public void processCombination(int[] combination, SortType sortType) {
        int[] matching = MatchingCalculator.countMatches(combination, histBox);
        int score = ScoreCounter.countScore(gameType, matching);
        BigCombination bigCombination = new BigCombination(combination, matching, score);

        if (bigCombinations.size() < combinationsAmountLimit) {
            // while list is not full add each..
            bigCombinations.add(bigCombination);
            // .. and renew minimal & maximal score
            if (minimalScore > score) {
                minimalScore = score;
            }
            if (maximalScore < score) {
                maximalScore = score;
            }
        } else {
            // when list is full add only if score is big (ASCENDING-type) or small (DESCENDING-type) enough..
            // .. and rid the line with the smallest or biggest score
            if (sortType == SortType.ASCENDING && score > minimalScore) {
                bigCombinations.add(bigCombination);
                Collections.sort(bigCombinations);
                minimalScore = bigCombinations.get(1).getScore();
                bigCombinations.remove(0);
            }
            if (sortType == SortType.DESCENDING && score < maximalScore) {
                bigCombinations.add(bigCombination);
                Collections.sort(bigCombinations);
                int lastIndex = bigCombinations.size() - 1;
                maximalScore = bigCombinations.get(lastIndex - 1).getScore();
                bigCombinations.remove(lastIndex);
            }
        }

    }

    /**
     * It returns quantity of ball-popping in format DD.dd where DD is quantity and
     * .dd is ball-id divided by 100.0
     */
    public double[] countFrequencyes() {
        // Count Frequency of balls
        int[] counter = new int[gameType.getGameSetSize() + 1];

        for (BigCombination bigCombination : bigCombinations) {
            int[] combination = bigCombination.getCombination();
            for (int ball : combination) {
                ++counter[ball];
            }
        }
        // prepare report
        double[] freqReport = new double[gameType.getGameSetSize() + 1];
        for (int i = 1; i < counter.length; i++) {
            freqReport[i] = counter[i] + 0.01 * i;
        }
        Arrays.sort(freqReport);
        return freqReport;
    }

    public String reportPlayCombinations() {
        StringBuilder sb = new StringBuilder(bigCombinations.size() + 4);
        if (bigCombinations.isEmpty()) {
            return "Zero Lines in Report!";
        }
        int combSetSize = bigCombinations.get(0).getCombinationLength();
        int gameSetSize = gameType.getGameSetSize();

        for (int i = 0; i < bigCombinations.size(); i++) {
            sb.append(Serv.normIntX(i + 1, 3, " ")).append(" ").append("  ").append(combSetSize).append("/")
                    .append(gameSetSize).append("\t").append(bigCombinations.get(i).report()).append("  ")
                    .append(System.lineSeparator());

        }

        return sb.substring(0, sb.length() - 1);
    }

    public int[] getNextLine() {
        return nextLine;
    }
}
