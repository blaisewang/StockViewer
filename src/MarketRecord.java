class MarketRecord {
    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    MarketRecord(String date, double open, double high, double low, double close, double volume) {
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

    double getVolume() {
        return volume;
    }

}
