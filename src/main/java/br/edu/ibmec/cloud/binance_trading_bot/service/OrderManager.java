package br.edu.ibmec.cloud.binance_trading_bot.service;

import br.edu.ibmec.cloud.binance_trading_bot.model.BotConfig;
import com.binance.connector.client.SpotClient;
import com.binance.connector.client.exceptions.BinanceClientException;
import com.binance.connector.client.exceptions.BinanceConnectorException;
import com.binance.connector.client.exceptions.BinanceServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderManager {
    private static final Logger logger = LoggerFactory.getLogger(OrderManager.class);
    
    private final SpotClient spotClient;
    private final BotConfig config;
    
    public OrderManager(SpotClient spotClient, BotConfig config) {
        this.spotClient = spotClient;
        this.config = config;
    }
    
    public void executeMarketOrder(String symbol, String side, BigDecimal quantity) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("symbol", symbol);
            parameters.put("side", side);
            parameters.put("type", "MARKET");
            parameters.put("quantity", quantity.toString());
            
            String result = spotClient.createTrade().newOrder(parameters);
            logger.info("Ordem de mercado executada: {}", result);
        } catch (BinanceConnectorException | BinanceServerException | BinanceClientException e) {
            logger.error("Erro ao executar ordem de mercado: {}", e.getMessage());
        }
    }
    
    public void executeLimitOrder(String symbol, String side, BigDecimal quantity, BigDecimal price) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("symbol", symbol);
            parameters.put("side", side);
            parameters.put("type", "LIMIT");
            parameters.put("timeInForce", "GTC");
            parameters.put("quantity", quantity.toString());
            parameters.put("price", price.toString());
            
            String result = spotClient.createTrade().newOrder(parameters);
            logger.info("Ordem limitada executada: {}", result);
        } catch (BinanceConnectorException | BinanceServerException | BinanceClientException e) {
            logger.error("Erro ao executar ordem limitada: {}", e.getMessage());
        }
    }
    
    public BigDecimal calculateStopLoss(BigDecimal entryPrice, boolean isLong) {
        BigDecimal stopLossPercentage = BigDecimal.valueOf(config.getStopLossPercentage());
        if (isLong) {
            return entryPrice.multiply(BigDecimal.ONE.subtract(stopLossPercentage.divide(BigDecimal.valueOf(100))));
        } else {
            return entryPrice.multiply(BigDecimal.ONE.add(stopLossPercentage.divide(BigDecimal.valueOf(100))));
        }
    }
    
    public BigDecimal calculateTakeProfit(BigDecimal entryPrice, boolean isLong) {
        BigDecimal takeProfitPercentage = BigDecimal.valueOf(config.getTakeProfitPercentage());
        if (isLong) {
            return entryPrice.multiply(BigDecimal.ONE.add(takeProfitPercentage.divide(BigDecimal.valueOf(100))));
        } else {
            return entryPrice.multiply(BigDecimal.ONE.subtract(takeProfitPercentage.divide(BigDecimal.valueOf(100))));
        }
    }
} 