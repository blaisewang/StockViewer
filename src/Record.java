class Record {
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    Record(String date, double open, double high, double low, double close, long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    String getDate() {
        return date;
    }

    double getOpen() {
        return open;
    }

    double getHigh() {
        return high;
    }

    double getLow() {
        return low;
    }

    double getClose() {
        return close;
    }

    long getVolume() {
        return volume;
    }

}
