package fun.kolowert.almond.alpha;

import fun.kolowert.almond.common.Combinator;
import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.FileHand;
import fun.kolowert.almond.serv.Serv;
import fun.kolowert.almond.serv.Sounder;
import fun.kolowert.almond.serv.Timer;
import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;

import java.time.LocalTime;

public class Alpha {

    private static final GameType GAME_TYPE = GameType.KENO;
    private static final SortType SORT_TYPE = SortType.ASCENDING;
    private static final int PLAY_SET = 5;
    private static final int HIST_DEEP = 8;
    private static final int HIST_SHIFT = 31;
    private static final int HIST_SHIFTS = 30;
    private static final int PROCESS_LIMIT = 3_000;

    private static final int WORKING_THREADS_AMOUNT = 3;

    private static final String DISPLAY_PREFIX_STUB = "----- |";

    private static final int[] HIT_RANGE_MASK = { 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64, 68, 72, 76, 80 };
            // { 16, 32, 48, 64, 80 }; // { 9, 18, 27, 36, 45 };

    public static void main(String[] args) {
        System.out
                .println("* Alpha Play * " + GAME_TYPE.name() + " * SortType:" + SORT_TYPE + " * " + Timer.dateTimeNow());
        Timer timer = new Timer();

        // Multiply playing
        ParamSet paramSet = new ParamSet(GAME_TYPE, SORT_TYPE, PLAY_SET, HIST_DEEP, HIST_SHIFT, HIST_SHIFTS,
                PROCESS_LIMIT, WORKING_THREADS_AMOUNT, DISPLAY_PREFIX_STUB, HIT_RANGE_MASK);

        multi(paramSet, false, true);

        overMulti(true, true);

        System.out.print("\n\nalpha finished ~ " + timer.reportExtended());
        Sounder.beep();
    }

    private static void overMulti(boolean doit, boolean letWriteResult) {
        if (!doit) { return; }
        BasePlayer basePlayer = new BasePlayer();
        System.out.println("## OVER MULTI ");
        System.out.println(Combinator.reportCombinationsQuantity(PLAY_SET, GAME_TYPE.getGameSetSize()));
        int[] histDeeps = new int[] { 8, 10 };
        int[] processLimitsKilo = new int[] { 1, 3, 6, 12, 16, 24 };
        StringBuilder resume = new StringBuilder("\nCoefficients --- over multi resume --- " + Timer.dateTimeNow());
        resume.append("\n" + ResultSet.csvCoefficientsHead() + ", |, " + ParamSet.csvHead());
        for (int histDeep : histDeeps) {
            for (int processLimitKilo : processLimitsKilo) {
                ParamSet paramSet = new ParamSet(GAME_TYPE, SORT_TYPE, PLAY_SET, histDeep, HIST_SHIFT, HIST_SHIFTS,
                        1000 * processLimitKilo, WORKING_THREADS_AMOUNT, DISPLAY_PREFIX_STUB, HIT_RANGE_MASK);

                ResultSet resultSet = basePlayer.playMulti(paramSet, false);

                if (letWriteResult) { new Writer(paramSet, resultSet).write(); }

                String overMultiResume = "\n" + resultSet.csvCoefficients() + ", |, " + paramSet.csvStamp()
                        + "  >>" + LocalTime.now().toString().substring(0, 8);

                resume.append(overMultiResume);
                System.out.print(overMultiResume);
            }
        }
        writeToFile(resume.toString());
    }

    private static void multi(ParamSet paramSet, boolean doit, boolean letWriteResult) {
        if (!doit) { return; }
        BasePlayer basePlayer = new BasePlayer();
        System.out.println("# MULTI " + paramSet);
        System.out.println(Combinator.reportCombinationsQuantity(PLAY_SET, GAME_TYPE.getGameSetSize()));

        ResultSet resultSet = basePlayer.playMulti(paramSet, true);

        if (letWriteResult) { new Writer(paramSet, resultSet).write(); }

        Display.displayResultSet(paramSet, resultSet);
    }

    private static void writeToFile(String input) {
        double t = .000_000_01 * System.currentTimeMillis();
        String timeMark = "_" + (int) (10_000 * (t - (int) t));
        String path = "src/main/resources/result/" + GAME_TYPE.name() + PLAY_SET + "-resume" + timeMark + ".txt";
        FileHand fileHand = new FileHand(path);
        fileHand.write(input);
    }
}
