package fun.kolowert.almond.alpha;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fun.kolowert.almond.common.HistHandler;
import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.Serv;
import lombok.NonNull;

public class BasePlayer {
    private static final int MAIN_PAUSE = 175;
    private static final int SMOOTHING_PAUSE = 175;
    private static final String TREAD_NAME_PREFIX = "almond";

    public ResultSet playMulti(@NonNull ParamSet paramSet, boolean letProgressBar) {

        HistHandler histHandler = new HistHandler(paramSet.gameType, 1, paramSet.histShift);
        int[] baseNextLine = histHandler.getNextLineOfHistBlock(paramSet.histShift);
        int baseNextLineId = baseNextLine[0];

        List<double[]> frequenciesTab = new ArrayList<>();
        List<int[]> histOrderResultTab = new ArrayList<>();
        int pause = MAIN_PAUSE * paramSet.playSet * paramSet.playSet;

        // main cycle
        for (int indexHistShift = paramSet.histShift + paramSet.histShifts - 1; indexHistShift >= paramSet.histShift;
             indexHistShift--) {

            int threadCounter = countWorkingThreads(TREAD_NAME_PREFIX);

            // wait if too lot threads ...
            while (threadCounter >= paramSet.workingThreads) {
                int fixedResultSize = histOrderResultTab.size();
                if (letProgressBar) { System.out.print("."); }
                try {
                    Thread.sleep(pause);
                    if (fixedResultSize != histOrderResultTab.size()) {
                        threadCounter -= histOrderResultTab.size() - fixedResultSize;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // do job
            Thread workingThread = new Thread(
                    new ConcurrentWorker(paramSet.getGameType(), paramSet.getSortType(), paramSet.getPlaySet(),
                            paramSet.getHistDeep(), indexHistShift, paramSet.getReportLimit(),
                            frequenciesTab, histOrderResultTab),"almond" + indexHistShift);
            workingThread.start();

            if (letProgressBar) { System.out.print(indexHistShift + ":"); }

            // just small pause for smooth running
            try {
                Thread.sleep(SMOOTHING_PAUSE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } // after main cycle

        // wait to end all working threads
        if (letProgressBar) { System.out.println(); }
        int threadCounter = countWorkingThreads(TREAD_NAME_PREFIX);
        while (threadCounter > 0) {
            int fixedResultSize = histOrderResultTab.size();
            if (letProgressBar) { System.out.print(","); }
            try {
                Thread.sleep(pause);
                if (fixedResultSize != histOrderResultTab.size()) {
                    threadCounter -= histOrderResultTab.size() - fixedResultSize;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (letProgressBar) { System.out.println("."); }

        paramSet.setId(baseNextLineId);
        return new ResultSet(baseNextLineId, frequenciesTab, histOrderResultTab, paramSet.hitRangesMask);
    }

    public void displayResultSet(ParamSet paramSet, ResultSet resultSet) {
        displayFrequenciesTab(paramSet, resultSet);
        displayHistOrderResultTab(paramSet, resultSet);
        displayOnRangesReport(paramSet, resultSet);
    }

    public void displayFrequenciesTab(ParamSet paramSet, ResultSet resultSet) {
        List<double[]> frequenciesTab = resultSet.getFrequenciesTab();
        StringBuilder result = new StringBuilder();
        for (double[] frequencies : frequenciesTab) {
            StringBuilder sb = new StringBuilder(6 * frequencies.length);
            for (int i = frequencies.length - 1; i >= 0; i--) {
                int order = (int) frequencies[i];
                int preball = (int) (100 * (frequencies[i] - order) + 0.005);
                String ball = Serv.normIntX(preball, 2, "0");
                sb.append(Serv.normIntX(order, 4, " ")).append("(").append(ball).append(")").append(" ");
            }
            result.append(sb).append(System.lineSeparator());
        }
        System.out.println("\nFrequencies Tab");
        System.out.println(Assistant.displayPlainHead(paramSet.gameType, "", "     "));
        System.out.println(result.substring(0, result.length() - 2));
    }

    private void displayHistOrderResultTab(ParamSet paramSet, ResultSet resultSet) {
        System.out.println();
        System.out.println("histOrderResultTab");
        System.out.println(Assistant.displayPlainHead(paramSet.gameType, paramSet.displayPrefix));
        List<int[]> histOrderResultTab = resultSet.getHistOrderResultTab();
        int counter = histOrderResultTab.size() - 1;
        for (int[] hits : histOrderResultTab) {
            System.out.println(Serv.displayIntArray(hits, paramSet.hitRangesMask,
                    Serv.normIntX(resultSet.getId() - counter--, 5, "0") + " |"));
        }
        int[] histOrderResultTabSum = new int[histOrderResultTab.get(0).length];
        for (int[] hits : histOrderResultTab) {
            for (int i = 0; i < hits.length; i++) {
                histOrderResultTabSum[i] += hits[i];
            }
        }
        System.out.println("\nSum of histOrderResultTab");
        System.out.println(Assistant.displayPlainHead(paramSet.gameType, paramSet.displayPrefix));
        System.out.println(Serv.displayIntArray(histOrderResultTabSum, paramSet.hitRangesMask, paramSet.displayPrefix));
    }

    private void displayOnRangesReport(ParamSet paramSet, ResultSet resultSet) {
        reportHitsOnRanges(resultSet.getHitsOnRanges(), resultSet.getHitRangesMask());
        System.out.println("\nzeroesOnHitsOnRanges: " + resultSet.getZeroesOnHitsOnRanges()
                + " / " + resultSet.getHitsOnRanges().size()
                + " >> zero coefficient: # " + Serv.normDoubleX(resultSet.getZeroCoefficient(), 2)
                + " # >>" + " on params: " + paramSet);
        System.out.println("WholeLinesOnHitsOnRanges: " + resultSet.getWholeLinesOnHitsOnRanges()
                + "  wholeLinesCoefficient: " + Serv.normDoubleX(resultSet.getWholeLinesCoefficient(), 2));
    }

    private void reportHitsOnRanges(List<int[]> hitsOnRanges, int[] hitsMask) {
        System.out.println("\nHits On Ranges");
        Assistant.displayRangesHead(hitsMask);
        Assistant.displayTab(hitsOnRanges);
        System.out.println("Sum");
        Assistant.displayHitsOnRangesResume(hitsOnRanges, hitsMask);
    }

    private int countWorkingThreads(String namePrefix) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        int threadCounter = 0;
        for (Thread th : threadSet) {
            if (th.getName().substring(0, 3).equals(namePrefix.substring(0, 3))) {
                ++threadCounter;
            }
        }
        return threadCounter;
    }
}
