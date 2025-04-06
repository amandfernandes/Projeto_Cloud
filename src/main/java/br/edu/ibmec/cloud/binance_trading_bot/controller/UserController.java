package br.edu.ibmec.cloud.binance_trading_bot.controller;
import br.edu.ibmec.cloud.binance_trading_bot.model.User;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserConfiguration;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserTrackingTicker;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserConfigurationRepository;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserRepository;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserTrackingTickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConfigurationRepository userConfigurationRepository;

    @Autowired
    private UserTrackingTickerRepository userTrackingTickerRepository;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        this.userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getById(@PathVariable("id") Integer id) {
        return this.userRepository.findById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("{id}/configuration")
    public ResponseEntity<User> associteConfiguration(@PathVariable("id") Integer id, @RequestBody UserConfiguration configuration) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Cria a configuração na base de dados
        this.userConfigurationRepository.save(configuration);

        //Associa a configuração ao usuario
        User user = optionalUser.get();
        user.getConfigurations().add(configuration);
        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

    @PostMapping("{id}/tracking-ticker")
    public ResponseEntity<User> associateTicker(@PathVariable("id") Integer id, @RequestBody UserTrackingTicker ticker) {
        Optional<User> optionalUser = this.userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        // Verifica se já existe um ticker com o mesmo símbolo para este usuário
        boolean symbolExists = user.getTrackingTickers()
            .stream()
            .anyMatch(existingTicker -> existingTicker.getSymbol().equals(ticker.getSymbol()));

        if (symbolExists) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Cria a configuração na base de dados
        this.userTrackingTickerRepository.save(ticker);

        // Associa a configuração ao usuario
        user.getTrackingTickers().add(ticker);
        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("{userId}/tracking-ticker/{tickerId}")
    public ResponseEntity<User> removeTicker(
            @PathVariable("userId") Integer userId,
            @PathVariable("tickerId") Integer tickerId) {
        
        // Busca o usuário
        Optional<User> optionalUser = this.userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();

        // Busca o ticker
        Optional<UserTrackingTicker> optionalTicker = this.userTrackingTickerRepository.findById(tickerId);
        if (optionalTicker.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserTrackingTicker tickerToRemove = optionalTicker.get();

        // Verifica se o ticker pertence ao usuário
        boolean tickerBelongsToUser = user.getTrackingTickers()
            .stream()
            .anyMatch(ticker -> ticker.getId().equals(tickerId));

        if (!tickerBelongsToUser) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Remove o ticker da lista do usuário
        user.getTrackingTickers().removeIf(ticker -> ticker.getId().equals(tickerId));
        userRepository.save(user);

        // Remove o ticker do banco de dados
        userTrackingTickerRepository.delete(tickerToRemove);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("{userId}/configuration/{configId}")
    public ResponseEntity<User> removeConfiguration(
            @PathVariable("userId") Integer userId,
            @PathVariable("configId") Integer configId) {
        
        // Busca o usuário
        Optional<User> optionalUser = this.userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();

        // Busca a configuração
        Optional<UserConfiguration> optionalConfig = this.userConfigurationRepository.findById(configId);
        if (optionalConfig.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserConfiguration configToRemove = optionalConfig.get();

        // Verifica se a configuração pertence ao usuário
        boolean configBelongsToUser = user.getConfigurations()
            .stream()
            .anyMatch(config -> config.getId().equals(configId));

        if (!configBelongsToUser) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Remove a configuração da lista do usuário
        user.getConfigurations().removeIf(config -> config.getId().equals(configId));
        userRepository.save(user);

        // Remove a configuração do banco de dados
        userConfigurationRepository.delete(configToRemove);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
