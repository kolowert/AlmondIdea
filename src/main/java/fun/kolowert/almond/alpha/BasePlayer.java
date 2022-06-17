package fun.kolowert.almond.alpha;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fun.kolowert.almond.common.HistHandler;
import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
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
