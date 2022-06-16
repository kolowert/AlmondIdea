package fun.kolowert.almond.alpha;

import fun.kolowert.almond.common.Combinator;
import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.Sounder;
import fun.kolowert.almond.serv.Timer;
import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;

import java.time.LocalDate;

public class Alpha {

    private static final GameType GAME_TYPE = GameType.MAXI;
    private static final SortType SORT_TYPE = SortType.ASCENDING;
    private static final int PLAY_SET = 5;
    private static final int HIST_DEEP = 12;
    private static final int HIST_SHIFT = 1;
    private static final int HIST_SHIFTS = 3;
    private static final int REPORT_LIMIT = 6_000;

    private static final int WORKING_THREADS_AMOUNT = 3;

    private static final String DISPLAY_PREFIX_STUB = "----- |";

    private static final int[] HIT_RANGE_MASK = {9, 18, 27, 36, 45};

    public static void main(String[] args) {
        System.out
                .println("* Alpha Play * " + GAME_TYPE.name() + " * SortType:" + SORT_TYPE + " * " + LocalDate.now());
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
        int[] histDeeps = new int[] { 9, 12, 18, 24, 36, 48, 60, 90 };
        int[] reportLimits = new int[] { 1_000, 3_000, 6_000, 9_000, 12_000, 16_000 };
        for (int histDeep : histDeeps) {
            for (int reportLimit : reportLimits) {
                ParamSet paramSet = new ParamSet(GAME_TYPE, SORT_TYPE, PLAY_SET, histDeep, HIST_SHIFT, HIST_SHIFTS,
                        reportLimit, WORKING_THREADS_AMOUNT, DISPLAY_PREFIX_STUB, HIT_RANGE_MASK);
                ResultSet resultSet = basePlayer.playMulti(paramSet, true);
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
