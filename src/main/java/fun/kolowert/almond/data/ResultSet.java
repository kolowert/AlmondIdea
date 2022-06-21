package fun.kolowert.almond.data;

import fun.kolowert.almond.alpha.Display;
import fun.kolowert.almond.serv.Count;
import fun.kolowert.almond.serv.Serv;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResultSet {

    private final int id;
    private final List<double[]> frequenciesTab;
    private final List<int[]> histOrderResultTab;
    private final int[] histOrderResultTabSum;

    private int[] hitRangesMask;
    private List<int[]> hitsOnRanges;
    private int[] hitsOnRangesSum;
    private double[] avgHitsOnRanges;

    private int zeroesOnHitsOnRanges;
    private double zeroCoefficient;
    private int wholeLinesOnHitsOnRanges;
    private double wholeLinesCoefficient;

    public ResultSet(int id, List<double[]> frequenciesTab, @NonNull List<int[]> histOrderResultTab, int[] hitRangesMask) {
        this.id = id;
        this.frequenciesTab = frequenciesTab;
        this.histOrderResultTab = histOrderResultTab;
        histOrderResultTabSum = makeHitsOrderResultTabSum(histOrderResultTab);
        this.hitRangesMask = hitRangesMask;
        countResultsOnMask(hitRangesMask);
    }

    private static List<int[]> calculateHitsOnRanges(List<int[]> tab, int[] mask) {
        List<int[]> result = new ArrayList<>();

        for (int[] line : tab) {
            int[] sectorSums = new int[mask.length];
            for (int maskIndex = 0; maskIndex < mask.length; maskIndex++) {
                for (int i = 0; i < line.length; i++) {
                    if (i < mask[maskIndex]) {
                        sectorSums[maskIndex] += line[i];
                    }
                }
            }
            int backSum = 0;
            for (int i = 1; i < sectorSums.length; i++) {
                backSum += sectorSums[i - 1];
                sectorSums[i] = sectorSums[i] - backSum;
            }
            result.add(sectorSums);
        }
        return result;
    }

    public static String csvCoefficientsHead() {
        return "id,zeroCoeff,wholeLineCoeff,wholelines,lines,deviation%, avgHits";
    }

    public String csvCoefficients() {
        return "" + id + ", " + Serv.normDoubleX(zeroCoefficient, 2)
                + ", " + Serv.normDoubleX(wholeLinesCoefficient, 2)
                + ", " + wholeLinesOnHitsOnRanges + "," + hitsOnRanges.size()
                + ", " + Serv.normDoubleX(Count.countStandardDeviationPercent(avgHitsOnRanges), 2, 1)
                + ", " + Display.reportDoubleArray(avgHitsOnRanges);
    }

    public String reportCoefficients() {
        return "id:" + id + "  zeroCoeff: " + Serv.normDoubleX(zeroCoefficient, 2)
                + "  wholeLineCoeff: " + Serv.normDoubleX(wholeLinesCoefficient, 2)
                + "  lines: " + wholeLinesOnHitsOnRanges + "|" + hitsOnRanges.size()
                + "  hitsDeviation % " + Serv.normDoubleX(Count.countStandardDeviationPercent(avgHitsOnRanges), 1);
    }

    public void recountResultsOnMask(int[] hitRangesMask) {
        this.hitRangesMask = hitRangesMask;
        countResultsOnMask(hitRangesMask);
    }

    private void countResultsOnMask(int[] hitRangesMask) {
        hitsOnRanges = calculateHitsOnRanges(histOrderResultTab, hitRangesMask);
        hitsOnRangesSum = countHitsOnRangesSum(hitsOnRanges, hitRangesMask);
        avgHitsOnRanges = countHitsOnRangesCoef(hitsOnRanges, hitsOnRangesSum, hitRangesMask);

        zeroesOnHitsOnRanges = countZeroesOnHitsOnRanges(hitsOnRanges);
        zeroCoefficient = 1.0 * zeroesOnHitsOnRanges / hitsOnRanges.size();
        wholeLinesOnHitsOnRanges = countWholeLinesOnHitsOnRanges(hitsOnRanges);
        wholeLinesCoefficient = 1.0 * wholeLinesOnHitsOnRanges / hitsOnRanges.size();
    }

    private double[] countHitsOnRangesCoef(List<int[]> hitsOnRanges, int[] sumLine, int[] mask) {
        double[] coef = new double[mask.length];
        int size = hitsOnRanges.size();
        for (int i = 0; i < sumLine.length && i < mask.length; i++) {
            coef[i] = 1.0 * sumLine[i] / size;
        }
        return coef;
    }

    private int[] countHitsOnRangesSum(List<int[]> hitsOnRanges, int[] mask) {
        int[] sumLine = new int[mask.length];
        for (int[] line : hitsOnRanges) {
            for (int i = 0; i < line.length && i < sumLine.length; i++) {
                sumLine[i] += line[i];
            }
        }
        return sumLine;
    }

    private int countWholeLinesOnHitsOnRanges(List<int[]> hitsOnRanges) {
        int counter = 0;
        for (int[] line : hitsOnRanges) {
            ++counter;
            for (int n : line) {
                if (n == 0) {
                    --counter;
                    break;
                }
            }
        }
        return counter;
    }

    private int countZeroesOnHitsOnRanges(List<int[]> hitsOnRanges) {
        int counter = 0;
        for (int[] line : hitsOnRanges) {
            for (int n : line) {
                if (n == 0) {
                    ++counter;
                }
            }
        }
        return counter;
    }

    private int[] makeHitsOrderResultTabSum(List<int[]> histOrderResultTab) {
        int[] hitsOrderResultTabSum = new int[histOrderResultTab.get(0).length];
        for (int[] hits : histOrderResultTab) {
            for (int i = 0; i < hits.length; i++) {
                hitsOrderResultTabSum[i] += hits[i];
            }
        }
        return hitsOrderResultTabSum;
    }
}
