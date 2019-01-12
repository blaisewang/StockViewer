class Range {

    private final double max;
    private final double min;
    private final double range;

    Range(double max, double min) {
        this.max = max;
        this.min = min;
        this.range = max - min;
    }

    double getMax() {
        return max;
    }

    double getMin() {
        return min;
    }

    double getRange() {
        return range;
    }

}
