package fun.kolowert.almond.alpha;

import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;
import fun.kolowert.almond.serv.FileHand;
import fun.kolowert.almond.serv.Timer;
import fun.kolowert.almond.type.SortType;

public class Writer {

    private final ParamSet paramSet;
    private final ResultSet resultSet;

    public Writer(ParamSet paramSet, ResultSet resultSet) {
        this.paramSet = paramSet;
        this.resultSet = resultSet;
    }

    public void write() {
        String path = preparePath();
        FileHand fileHand = new FileHand(path);
        String info = prepareInfo();
        fileHand.write(info);
    }

    private String preparePath() {
        double t = .000_000_01 * System.currentTimeMillis();
        String timeMark = "_" + (int) (10_000 * (t - (int) t));
        String folder = "src/main/resources/result/";
        String sortMark = paramSet.sortType == SortType.ASCENDING ? "-A-" : "-D-";
        String name = paramSet.getGameType().name();
        return folder + name + sortMark + paramSet.getId()
                + "_" + paramSet.playSet + "-" + paramSet.histDeep
                + "_" + paramSet.histShift + "-" + paramSet.histShifts
                + "-" + (int) (0.001 * paramSet.processLimit)
                + timeMark + ".txt";
    }

    private String prepareInfo() {
        StringBuilder info = new StringBuilder();
        info.append(prepareHead());
        info.append(Preparatory.prepareFrequenciesTab(paramSet, resultSet)).append(System.lineSeparator());
        info.append(Preparatory.prepareHitsOnFrequenciesTab(resultSet)).append(System.lineSeparator());
        info.append(Preparatory.prepareHistOrderResultTab(paramSet, resultSet));
        info.append(Preparatory.prepareHitsOnRanges(resultSet));
        info.append(System.lineSeparator());
        info.append("\n" + ResultSet.csvCoefficientsHead() + ", |, " + ParamSet.csvHead());
        info.append("\n" + resultSet.csvCoefficients() + ", |, " + paramSet.csvStamp());
        return info.toString();
    }

    private String prepareHead() {
        StringBuilder head = new StringBuilder();
        head.append("Alpha Play * ").append(Timer.dateTimeNow()).append(System.lineSeparator());
        head.append(ParamSet.csvHead()).append(System.lineSeparator());
        head.append(paramSet.csvStamp()).append(System.lineSeparator());
        return head.toString();
    }
}
