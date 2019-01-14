/**
 * Range data structure
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
