package fun.kolowert.almond.data;

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

    public String reportCoefficients() {
        return "id:" + id + "  zeroCoeff: " + Serv.normDoubleX(zeroCoefficient, 2)
                + "  wholeLineCoeff: " + Serv.normDoubleX(wholeLinesCoefficient, 2)
                + "  lines: " + wholeLinesOnHitsOnRanges + "|" + hitsOnRanges.size();
    }

    public void recountResultsOnMask(int[] hitRangesMask) {
        this.hitRangesMask = hitRangesMask;
        countResultsOnMask(hitRangesMask);
    }

    private void countResultsOnMask(int[] hitRangesMask) {
        hitsOnRanges = calculateHitsOnRanges(histOrderResultTab, hitRangesMask);
        zeroesOnHitsOnRanges = countZeroesOnHitsOnRanges(hitsOnRanges);
        zeroCoefficient = 1.0 * zeroesOnHitsOnRanges / hitsOnRanges.size();
        wholeLinesOnHitsOnRanges = countWholeLinesOnHitsOnRanges(hitsOnRanges);
        wholeLinesCoefficient = 1.0 * wholeLinesOnHitsOnRanges / hitsOnRanges.size();
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
