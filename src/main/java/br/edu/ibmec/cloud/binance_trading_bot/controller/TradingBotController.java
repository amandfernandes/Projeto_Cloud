package br.edu.ibmec.cloud.binance_trading_bot.controller;

import br.edu.ibmec.cloud.binance_trading_bot.model.BotConfig;
import br.edu.ibmec.cloud.binance_trading_bot.service.TradingBot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading-bot")
public class TradingBotController {
    
    private final TradingBot tradingBot;
    
    public TradingBotController(TradingBot tradingBot) {
        this.tradingBot = tradingBot;
    }
    
    @PostMapping("/start")
    public ResponseEntity<String> startBot(@RequestBody BotConfig config) {
        tradingBot.start();
        return ResponseEntity.ok("Bot iniciado com sucesso");
    }
    
    @PostMapping("/stop")
    public ResponseEntity<String> stopBot() {
        tradingBot.stop();
        return ResponseEntity.ok("Bot parado com sucesso");
    }
} 