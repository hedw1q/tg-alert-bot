package ru.hedw1q.TgBot.telegram;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * @author hedw1q
 */
@Configuration
public class TgBotTestConfig {

    @Bean
    public TgBot tgBotInit() throws TelegramApiException {
        TgBot tgBot = new TgBot();
        new TelegramBotsApi(DefaultBotSession.class).registerBot(tgBot);
        return tgBot;
    }

}
