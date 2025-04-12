package br.edu.ibmec.cloud.binance_trading_bot.service;

import br.edu.ibmec.cloud.binance_trading_bot.model.BotConfig;
import br.edu.ibmec.cloud.binance_trading_bot.model.Candlestick;
import com.binance.connector.client.SpotClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TradingBot {
    private static final Logger logger = LoggerFactory.getLogger(TradingBot.class);
    
    private final SpotClient spotClient;
    private final BotConfig config;
    private final TechnicalPatternDetector patternDetector;
    private final OrderManager orderManager;
    private final ScheduledExecutorService scheduler;
    
    public TradingBot(SpotClient spotClient, BotConfig config, 
                     TechnicalPatternDetector patternDetector, 
                     OrderManager orderManager) {
        this.spotClient = spotClient;
        this.config = config;
        this.patternDetector = patternDetector;
        this.orderManager = orderManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public void start() {
        logger.info("Iniciando Trading Bot...");
        
        // Agenda a verificação de padrões para cada par de trading
        for (String pair : config.getTradingPairs()) {
            scheduler.scheduleAtFixedRate(
                () -> checkTradingPair(pair),
                0,
                1,
                TimeUnit.MINUTES
            );
        }
    }
    
    private void checkTradingPair(String pair) {
        try {
            // Obtém os dados das velas
            List<Candlestick> candles = getCandlestickData(pair);
            
            // Verifica padrões de trading
            if (patternDetector.isBullishEngulfing(candles)) {
                logger.info("Padrão de engolfo de alta detectado para {}", pair);
                executeBuyOrder(pair, candles.get(0).getClose());
            } else if (patternDetector.isBearishEngulfing(candles)) {
                logger.info("Padrão de engolfo de baixa detectado para {}", pair);
                executeSellOrder(pair, candles.get(0).getClose());
            } else if (patternDetector.isInsideBarBreakout(candles)) {
                logger.info("Breakout de inside bar detectado para {}", pair);
                if (candles.get(0).getClose().compareTo(candles.get(1).getHigh()) > 0) {
                    executeBuyOrder(pair, candles.get(0).getClose());
                } else {
                    executeSellOrder(pair, candles.get(0).getClose());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao verificar par {}: {}", pair, e.getMessage());
        }
    }
    
    private List<Candlestick> getCandlestickData(String pair) {
        // Implementar a lógica para obter dados das velas da Binance
        // Retornar lista de Candlestick
        return List.of(); // Placeholder
    }
    
    private void executeBuyOrder(String pair, BigDecimal price) {
        if (config.isUseMarketOrders()) {
            orderManager.executeMarketOrder(pair, "BUY", calculateQuantity(pair, price));
        } else {
            orderManager.executeLimitOrder(pair, "BUY", calculateQuantity(pair, price), price);
        }
    }
    
    private void executeSellOrder(String pair, BigDecimal price) {
        if (config.isUseMarketOrders()) {
            orderManager.executeMarketOrder(pair, "SELL", calculateQuantity(pair, price));
        } else {
            orderManager.executeLimitOrder(pair, "SELL", calculateQuantity(pair, price), price);
        }
    }
    
    private BigDecimal calculateQuantity(String pair, BigDecimal price) {
        // Implementar lógica para calcular a quantidade baseada no saldo disponível
        return BigDecimal.ONE; // Placeholder
    }
    
    public void stop() {
        logger.info("Parando Trading Bot...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
} 