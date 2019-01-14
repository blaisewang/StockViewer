/**
 * Range data structure
 *
 * @author Xudong Wang (xwang199@sheffield.ac.uk)
 * @version 1.0 08 January 2019
 */


class Range {

    private final double max;
    private final double min;
    private final double range;

    /**
     * Class constructor
     *
     * @param max value
     * @param min value
     */
    Range(double max, double min) {
        this.max = max;
        this.min = min;
        this.range = max - min;
    }

    /**
     * @return max
     */
    double getMax() {
        return max;
    }

    /**
     * @return min
     */
    double getMin() {
        return min;
    }

    /**
     * @return range
     */
    double getRange() {
        return range;
    }

}
