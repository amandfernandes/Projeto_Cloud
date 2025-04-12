package br.edu.ibmec.cloud.binance_trading_bot.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Candlestick {
    private final Instant openTime;
    private final BigDecimal open;
    private final BigDecimal high;
    private final BigDecimal low;
    private final BigDecimal close;
    private final BigDecimal volume;
    private final Instant closeTime;

    public Candlestick(Instant openTime, BigDecimal open, BigDecimal high, 
                      BigDecimal low, BigDecimal close, BigDecimal volume, 
                      Instant closeTime) {
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
    }

    public Instant getOpenTime() {
        return openTime;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public Instant getCloseTime() {
        return closeTime;
    }

    public boolean isBullish() {
        return close.compareTo(open) > 0;
    }

    public boolean isBearish() {
        return close.compareTo(open) < 0;
    }
} 