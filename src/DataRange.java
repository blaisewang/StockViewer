class DataRange {

    private final double max;
    private final double min;
    private final double range;

    DataRange(double max, double min) {
        this.max = max;
        this.min = min;
        this.range = max - min;
    }

    double getMaxRange() {
        return max;
    }

    double getMinRange() {
        return min;
    }

    double getRange() {
        return range;
    }

}
