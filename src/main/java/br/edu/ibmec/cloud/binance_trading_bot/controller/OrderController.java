package br.edu.ibmec.cloud.binance_trading_bot.controller;
import br.edu.ibmec.cloud.binance_trading_bot.model.User;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserOrderReport;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserOrderReportRepository;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserRepository;
import br.edu.ibmec.cloud.binance_trading_bot.request.OrderRequest;
import br.edu.ibmec.cloud.binance_trading_bot.response.OrderResponse;
import br.edu.ibmec.cloud.binance_trading_bot.service.BinanceIntegration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("{id}/order")
public class OrderController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserOrderReportRepository userOrderReportRepository;

    @Autowired
    private BinanceIntegration binanceIntegration;


    @PostMapping
    public ResponseEntity<OrderResponse> sendOrder(@PathVariable("id") int id, @RequestBody OrderRequest request) {
        // Busca o usuário
        User user = getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Configura a integração com a Binance
        configureBinanceIntegration(user);

        try {
            // Processa a ordem na Binance
            OrderResponse response = processOrderInBinance(request);
            
            // Processa o relatório da ordem
            processOrderReport(request, response, user);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private User getUserById(int id) {
        return repository.findById(id).orElse(null);
    }

    private void configureBinanceIntegration(User user) {
        binanceIntegration.setAPI_KEY(user.getBinanceApiKey());
        binanceIntegration.setSECRET_KEY(user.getBinanceSecretKey());
    }

    private OrderResponse processOrderInBinance(OrderRequest request) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        String result = binanceIntegration.createMarketOrder(
            request.getSymbol(),
            request.getQuantity(),
            request.getSide()
        );
        
        return objectMapper.readValue(result, OrderResponse.class);
    }

    private void processOrderReport(OrderRequest request, OrderResponse response, User user) {
        if ("BUY".equals(request.getSide())) {
            processBuyOrder(request, response, user);
        } else if ("SELL".equals(request.getSide())) {
            processSellOrder(request, response, user);
        }
    }

    private void processBuyOrder(OrderRequest request, OrderResponse response, User user) {
        UserOrderReport report = new UserOrderReport();
        report.setSymbol(request.getSymbol());
        report.setQuantity(request.getQuantity());
        report.setBuyPrice(response.getFills().get(0).getPrice());
        report.setDtOperation(LocalDateTime.now());

        userOrderReportRepository.save(report);
        user.getOrderReports().add(report);
        repository.save(user);
    }

    private void processSellOrder(OrderRequest request, OrderResponse response, User user) {
        UserOrderReport order = findOpenOrderForSymbol(request.getSymbol(), user);
        if (order != null) {
            order.setSellPrice(response.getFills().get(0).getPrice());
            userOrderReportRepository.save(order);
        }
    }

    private UserOrderReport findOpenOrderForSymbol(String symbol, User user) {
        return user.getOrderReports().stream()
            .filter(item -> item.getSymbol().equals(symbol) && item.getSellPrice() == 0)
            .findFirst()
            .orElse(null);
    }

}
