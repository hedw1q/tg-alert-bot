package ru.hedw1q.TgBot.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.hedw1q.TgBot.telegram.commands.CheckStreamStatusCommand;

/**
 * @author hedw1q
 */
@Component
public class TgBotInitilizer {

    private TgBot tgBot;
    private TelegramBotsApi telegramBotsApi;
    private static final Logger logger = LoggerFactory.getLogger(TgBotInitilizer.class);


    @Autowired
    public TgBotInitilizer(TgBot tgBot) throws TelegramApiException{
        try{
            new TelegramBotsApi(DefaultBotSession.class).registerBot(tgBot);

            tgBot.register(new CheckStreamStatusCommand("status", "Статус стрима"));
            logger.info("Connected to telegram");
        } catch (TelegramApiException ex){
            logger.error(ex.getMessage());
        }

    }
}
