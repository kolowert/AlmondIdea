package fun.kolowert.almond.serv;

public class Count {

    private Count() {}

    public static double countStandardDeviationPercent(double[] input) {
        double avgInput = countAverage(input);
        double deviation = countStandardDeviation(input);
        if (avgInput == 0.0) { return 0.0; }
        return 100.0 * deviation / avgInput;
    }

    public static double countStandardDeviation(double[] input) {
        double inputAverage = countAverage(input);
        double[] deviations = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            deviations[i] = Math.pow((input[i] - inputAverage), 2);
        }
        double variance = countAverage(deviations);
        return Math.sqrt(variance);
    }

    public static double countAverage(double[] input) {
        double inputSum = 0.0;
        for (double d : input) {
            inputSum += d;
        }
        return inputSum / input.length;
    }
}
