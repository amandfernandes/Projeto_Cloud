package br.edu.ibmec.cloud.binance_trading_bot.model;

import java.util.List;

public class BotConfig {
    private final List<String> tradingPairs;
    private final String timeframe;
    private final double stopLossPercentage;
    private final double takeProfitPercentage;
    private final boolean useMarketOrders;
    private final int maxOpenPositions;

    public BotConfig(List<String> tradingPairs, String timeframe, 
                    double stopLossPercentage, double takeProfitPercentage,
                    boolean useMarketOrders, int maxOpenPositions) {
        this.tradingPairs = tradingPairs;
        this.timeframe = timeframe;
        this.stopLossPercentage = stopLossPercentage;
        this.takeProfitPercentage = takeProfitPercentage;
        this.useMarketOrders = useMarketOrders;
        this.maxOpenPositions = maxOpenPositions;
    }

    public List<String> getTradingPairs() {
        return tradingPairs;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public double getStopLossPercentage() {
        return stopLossPercentage;
    }

    public double getTakeProfitPercentage() {
        return takeProfitPercentage;
    }

    public boolean isUseMarketOrders() {
        return useMarketOrders;
    }

    public int getMaxOpenPositions() {
        return maxOpenPositions;
    }
} 