package fun.kolowert.almond.alpha;

import fun.kolowert.almond.common.Combinator;
import fun.kolowert.almond.common.HistHandler;
import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;

import java.util.List;

public class ConcurrentWorker implements Runnable {

    private final GameType gameType;
    private final SortType sortType;
    private final int playSet;
    private final int reportLimit;

    private HistHandler histHandler;
    private int[] nextLine;
    private List<double[]> frequenciesTab;
    private List<int[]> histOrderResultTab;

    public ConcurrentWorker(GameType gameType, SortType sortType, int playSet, int histDeep, int histShift,
                            int reportLimit, List<double[]> frequenciesTabRez, List<int[]> histOrderResultTabRez) {
        this.gameType = gameType;
        this.sortType = sortType;
        this.playSet = playSet;
        this.reportLimit = reportLimit;

        this.histHandler = new HistHandler(gameType, histDeep, histShift);
        this.nextLine = histHandler.getNextLineOfHistBlock(histShift);
        this.frequenciesTab = frequenciesTabRez;
        this.histOrderResultTab = histOrderResultTabRez;
    }

    @Override
    public void run() {
        double[] frequencies = countFrequencies();
        int[] histOrder = countHitsOrder(frequencies);
        frequenciesTab.add(frequencies);
        histOrderResultTab.add(histOrder);
    }

    public double[] countFrequencies() {
        List<int[]> histBox = histHandler.getHistCombinations();
        Processor processor = new Processor(gameType, histBox, nextLine, reportLimit);
        Combinator combinator = new Combinator(playSet, gameType.getGameSetSize());
        while (!combinator.isFinished()) {
            int[] combination = combinator.makeNext();
            processor.processCombination(combination, sortType);
        }
        return processor.countFrequencyes();
    }

    public int[] countHitsOrder(double[] frequencies) {
        int[] ballSequence = Processor.extractBallSequence(frequencies);
        return countBallHitsInBallSequence(ballSequence);
    }

    private int[] countBallHitsInBallSequence(int[] ballSequence) {
        int length = ballSequence.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            if (isHit(ballSequence[i], nextLine)) {
                ++result[i];
            }
        }
        return result;
    }

    private boolean isHit(int ball, int[] nextLine) {
        for (int n : nextLine) {
            if (ball == n) {
                return true;
            }
        }
        return false;
    }
}
