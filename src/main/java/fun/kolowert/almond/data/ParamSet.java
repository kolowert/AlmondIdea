package fun.kolowert.almond.data;

import fun.kolowert.almond.type.GameType;
import fun.kolowert.almond.type.SortType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.Arrays;

@Data
@RequiredArgsConstructor
public class ParamSet {

    private int id = -1;
    public final GameType gameType;
    public final SortType sortType;
    public final int playSet;
    public final int histDeep;
    public final int histShift;
    public final int histShifts;
    public final int reportLimit;
    public final int workingThreads;
    public final String displayPrefix;
    public final int[] hitRangesMask;

    @Override
    public String toString() {
        return "id:" + id + " " + gameType + " " + sortType + " playSet:" + playSet + " histDeep:" + histDeep
                + " histLines:" + histShift + "/" + histShifts + " reportLimit:" + reportLimit + " hitRangesMask:"
                + Arrays.toString(hitRangesMask) + " threads:" + workingThreads + "   "
                + LocalTime.now().toString().substring(0, 8) + "  ";
    }

}
