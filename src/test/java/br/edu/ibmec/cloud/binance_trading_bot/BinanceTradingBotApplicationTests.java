package br.edu.ibmec.cloud.binance_trading_bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BinanceTradingBotApplicationTests {

	@Test
	void contextLoads() {
        BinanceTradingBotApplicationTests applicationTests = new BinanceTradingBotApplicationTests();
        assertDoesNotThrow(applicationTests::contextLoads, "contextLoads method should not throw any exception");
    }
}
