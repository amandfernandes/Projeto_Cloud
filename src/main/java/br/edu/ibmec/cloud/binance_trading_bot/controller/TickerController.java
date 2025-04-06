package br.edu.ibmec.cloud.binance_trading_bot.controller;
import br.edu.ibmec.cloud.binance_trading_bot.model.User;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserTrackingTicker;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserRepository;
import br.edu.ibmec.cloud.binance_trading_bot.response.TickerResponse;
import br.edu.ibmec.cloud.binance_trading_bot.service.BinanceIntegration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("{id}/tickers")
public class TickerController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BinanceIntegration binanceIntegration;

    @GetMapping
    public ResponseEntity<List<TickerResponse>> getTickers(@PathVariable("id") int id) {
        // Busca e valida o usuário
        User user = getUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            // Configura a integração com a Binance
            configureBinanceIntegration(user);
            
            // Obtém os tickers do usuário
            List<String> userTickers = extractUserTickers(user);
            
            // Busca as informações dos tickers na Binance
            return processTickerInformation(userTickers);
            
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

    private List<String> extractUserTickers(User user) {
        return user.getTrackingTickers()
            .stream()
            .map(UserTrackingTicker::getSymbol)
            .collect(Collectors.toList());
    }

    private ResponseEntity<List<TickerResponse>> processTickerInformation(List<String> tickers) throws Exception {
        String binanceResponse = binanceIntegration.getTickers(tickers);
        List<TickerResponse> tickerResponses = parseTickerResponse(binanceResponse);
        return new ResponseEntity<>(tickerResponses, HttpStatus.OK);
    }

    private List<TickerResponse> parseTickerResponse(String binanceResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(
            binanceResponse,
            new TypeReference<List<TickerResponse>>() {}
        );
    }

}
