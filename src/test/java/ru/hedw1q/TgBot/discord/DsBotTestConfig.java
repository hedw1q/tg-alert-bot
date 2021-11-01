package ru.hedw1q.TgBot.discord;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author hedw1q
 */
@TestConfiguration
public class DsBotTestConfig {

    @Bean
    DsBotInitializer initDSBot(){
        return new DsBotInitializer();
    }
}
