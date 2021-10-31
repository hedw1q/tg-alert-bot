package ru.hedw1q.TgBot.goodgame;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.hedw1q.TgBot.twitch.services.StreamServiceImpl;

/**
 * @author hedw1q
 */
@Configuration
public class GGBotTestConfig {

    @Bean
    public TgBot tgBotInit() throws TelegramApiException {
        TgBot tgBot = new TgBot();
        new TelegramBotsApi(DefaultBotSession.class).registerBot(tgBot);
        return tgBot;
    }
    @Bean
    public GGBot ggBotInit(){
        return new GGBot();
    }

    @Bean
    public StreamService streamServiceInit(){
        return new StreamServiceImpl();
    }
}
