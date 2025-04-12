package br.edu.ibmec.cloud.binance_trading_bot.service;

import br.edu.ibmec.cloud.binance_trading_bot.model.Candlestick;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicalPatternDetector {
    
    public boolean isBullishEngulfing(List<Candlestick> candles) {
        if (candles.size() < 2) return false;
        
        Candlestick current = candles.get(0);
        Candlestick previous = candles.get(1);
        
        return previous.isBearish() && 
               current.isBullish() &&
               current.getOpen().compareTo(previous.getClose()) < 0 &&
               current.getClose().compareTo(previous.getOpen()) > 0;
    }
    
    public boolean isBearishEngulfing(List<Candlestick> candles) {
        if (candles.size() < 2) return false;
        
        Candlestick current = candles.get(0);
        Candlestick previous = candles.get(1);
        
        return previous.isBullish() && 
               current.isBearish() &&
               current.getOpen().compareTo(previous.getClose()) > 0 &&
               current.getClose().compareTo(previous.getOpen()) < 0;
    }
    
    public boolean isInsideBar(List<Candlestick> candles) {
        if (candles.size() < 2) return false;
        
        Candlestick current = candles.get(0);
        Candlestick previous = candles.get(1);
        
        return current.getHigh().compareTo(previous.getHigh()) < 0 &&
               current.getLow().compareTo(previous.getLow()) > 0;
    }
    
    public boolean isInsideBarBreakout(List<Candlestick> candles) {
        if (!isInsideBar(candles)) return false;
        
        Candlestick current = candles.get(0);
        Candlestick previous = candles.get(1);
        
        // Breakout para cima
        if (current.getClose().compareTo(previous.getHigh()) > 0) {
            return true;
        }
        
        // Breakout para baixo
        if (current.getClose().compareTo(previous.getLow()) < 0) {
            return true;
        }
        
        return false;
    }
} 