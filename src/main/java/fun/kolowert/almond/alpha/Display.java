package fun.kolowert.almond.alpha;

import fun.kolowert.almond.data.ParamSet;
import fun.kolowert.almond.data.ResultSet;

public class Display {

    private Display() {
    }

    public static void displayResultSet(ParamSet paramSet, ResultSet resultSet) {
        String frequenciesTab = Preparatory.prepareFrequenciesTab(paramSet, resultSet);
        System.out.println(frequenciesTab);
        String hitsOnFrequenciesTab = Preparatory.prepareHitsOnFrequenciesTab(resultSet);
        System.out.println(hitsOnFrequenciesTab);
        String histOrderResultTab = Preparatory.prepareHistOrderResultTab(paramSet, resultSet);
        System.out.println(histOrderResultTab);
        String hitsOnRanges = Preparatory.prepareHitsOnRanges(resultSet);
        System.out.println(hitsOnRanges);
        System.out.print("\n" + ResultSet.csvCoefficientsHead() + ", |, " + ParamSet.csvHead());
        System.out.print("\n" + resultSet.csvCoefficients() + ", |, " + paramSet.csvStamp());
    }

}
