package ru.hedw1q.TgBot.discord;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hedw1q
 */
@Configuration
public class DsBotTestConfig {
    @Bean
    DsBotInitializer initDSBot(){
        return new DsBotInitializer();
    }
}
