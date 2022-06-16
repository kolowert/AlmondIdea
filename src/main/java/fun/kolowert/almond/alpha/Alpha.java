package fun.kolowert.almond.alpha;

import fun.kolowert.almond.common.Combinator;
import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.Sounder;
import fun.kolowert.almond.serv.Timer;
import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;

public class Alpha {

    private static final GameType GAME_TYPE = GameType.KENO;
    private static final SortType SORT_TYPE = SortType.ASCENDING;
    private static final int PLAY_SET = 3;
    private static final int HIST_DEEP = 12;
    private static final int HIST_SHIFT = 10;
    private static final int HIST_SHIFTS = 8;
    private static final int REPORT_LIMIT = 12_000;

    private static final int WORKING_THREADS_AMOUNT = 4;

    private static final String DISPLAY_PREFIX_STUB = "----- |";

    private static final int[] HIT_RANGE_MASK = { 5, 10, 15, 20, 25, 30, 35 };

    public static void main(String[] args) {
        System.out
                .println("* Alpha Play * " + GAME_TYPE.name() + " * SortType:" + SORT_TYPE + " * " + Timer.dateTimeNow());
        Timer timer = new Timer();

        // Multiply playing
        ParamSet paramSet = new ParamSet(GAME_TYPE, SORT_TYPE, PLAY_SET, HIST_DEEP, HIST_SHIFT, HIST_SHIFTS,
                REPORT_LIMIT, WORKING_THREADS_AMOUNT, DISPLAY_PREFIX_STUB, HIT_RANGE_MASK);
        multi(true, paramSet);

        overMulti(false);

        System.out.print("\nalpha finished ~ " + timer.reportExtended());
        Sounder.beep();
    }

    private static void overMulti(boolean doit) {
        if (!doit) { return; }
        BasePlayer basePlayer = new BasePlayer();
        System.out.println("## OVER MULTI ");
        System.out.println(Combinator.reportCombinationsQuantity(PLAY_SET, GAME_TYPE.getGameSetSize()));
        int[] histDeeps = new int[] { 8, 12, 18, 24, 36, 48, 60 };
        int[] reportLimits = new int[] { 3_000, 6_000, 9_000, 12_000, 16_000 };
        for (int histDeep : histDeeps) {
            for (int reportLimit : reportLimits) {
                ParamSet paramSet = new ParamSet(GAME_TYPE, SORT_TYPE, PLAY_SET, histDeep, HIST_SHIFT, HIST_SHIFTS,
                        reportLimit, WORKING_THREADS_AMOUNT, DISPLAY_PREFIX_STUB, HIT_RANGE_MASK);
                ResultSet resultSet = basePlayer.playMulti(paramSet, false);
                System.out.print("\n" + resultSet.reportCoefficients() + " onParams: " + paramSet);
            }
        }
    }

    private static void multi(boolean doit, ParamSet paramSet) {
        if (!doit) { return; }
        BasePlayer basePlayer = new BasePlayer();
        System.out.println("# MULTI " + paramSet);
        System.out.println(Combinator.reportCombinationsQuantity(PLAY_SET, GAME_TYPE.getGameSetSize()));

        ResultSet resultSet = basePlayer.playMulti(paramSet, true);
        basePlayer.displayResultSet(paramSet, resultSet);
        System.out.println("\n" + resultSet.reportCoefficients() + " onParams: " + paramSet);
    }
}
