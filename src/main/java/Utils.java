import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Utils {

    public static double[] cumSum(double[] arr) {
        double[] out = new double[arr.length];
        double cumSum = 0;
        for (int i = 0; i < arr.length; i++) {
            cumSum += arr[i];
            out[i] = cumSum;
        }
        return out;
    }

    /**
     * Choose a random index of the given array weighted by the value at each index. This is implemented
     * by designating certain ranges within [0,1] as belonging to an index, with the width of the range
     * varying depending on the index's value in the array. Whichever one of these ranges `rand` falls
     * into is the node that is returned.
     *
     * <p/>
     * For example:
     * <tt>
     * [[_____n1_____][__n2__][____________n3____________]]
     * 0========================.5========================1
     * </tt>
     * <ul>
     *     <li>Rand = 0.1 -> return n1</li>
     *     <li>Rand = 0.4 -> return n2</li>
     *     <li>Rand = 0.7 -> return n3</li>
     * </ul>
     *
     * @param arr An array of doubles >= 0 with at least one value >0
     * @param rand A uniform random double in [0,1)
     * @return A randomly selected index of the array weighted by the
     */
    public static int chooseRandomIndexByWeight(double[] arr, double rand) {
        double sum = Arrays.stream(arr).reduce(0.0, Double::sum);
        if  (sum <= 0) throw new IllegalArgumentException("Elements sum to invalid value: " + sum);
        double[] normalized = Arrays.stream(arr)
                .map(f -> f / sum)
                .toArray();
        double[] cumSum = cumSum(normalized);
        for (int i = 0; i < cumSum.length; i++) {
            if (rand < cumSum[i]) {
                return i;
            }
        }
        throw new RuntimeException("Did not select a value. This should be unreachable");
    }

}