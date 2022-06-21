package fun.kolowert.almond.alpha;

import fun.kolowert.almond.common.Combinator;
import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.Sounder;
import fun.kolowert.almond.serv.Timer;
import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;

import java.time.LocalTime;

public class Alpha {

    private static final GameType GAME_TYPE = GameType.KENO;
    private static final SortType SORT_TYPE = SortType.ASCENDING;
    private static final int PLAY_SET = 3;
    private static final int HIST_DEEP = 8;
    private static final int HIST_SHIFT = 1;
    private static final int HIST_SHIFTS = 3;
    private static final int PROCESS_LIMIT = 2_000;

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

        multi(true, paramSet, true);

        overMulti(false);

        System.out.print("\n\nalpha finished ~ " + timer.reportExtended());
        Sounder.beep();
    }

    private static void overMulti(boolean doit) {
        if (!doit) { return; }
        BasePlayer basePlayer = new BasePlayer();
        System.out.println("## OVER MULTI ");
        System.out.println(Combinator.reportCombinationsQuantity(PLAY_SET, GAME_TYPE.getGameSetSize()));
        int[] histDeeps = new int[] { 8, 10, 12, 16, 20 };
        int[] processLimitsKilo = new int[] { 1 };
        System.out.print("\nCoefficients");
        System.out.print("\n" + ResultSet.csvCoefficientsHead() + ", |, " + ParamSet.csvHead());
        for (int histDeep : histDeeps) {
            for (int processLimitKilo : processLimitsKilo) {
                ParamSet paramSet = new ParamSet(GAME_TYPE, SORT_TYPE, PLAY_SET, histDeep, HIST_SHIFT, HIST_SHIFTS,
                        1000 * processLimitKilo, WORKING_THREADS_AMOUNT, DISPLAY_PREFIX_STUB, HIT_RANGE_MASK);
                ResultSet resultSet = basePlayer.playMulti(paramSet, false);
                System.out.print("\n" + resultSet.csvCoefficients() + ", |, " + paramSet.csvStamp()
                        + "  >>" + LocalTime.now().toString().substring(0, 8));
            }
        }
    }

    private static void multi(boolean doit, ParamSet paramSet, boolean writeResult) {
        if (!doit) { return; }
        BasePlayer basePlayer = new BasePlayer();
        System.out.println("# MULTI " + paramSet);
        System.out.println(Combinator.reportCombinationsQuantity(PLAY_SET, GAME_TYPE.getGameSetSize()));

        ResultSet resultSet = basePlayer.playMulti(paramSet, true);

        if (writeResult) { new Writer(paramSet, resultSet).write(); }

        Display.displayResultSet(paramSet, resultSet);
    }
}
