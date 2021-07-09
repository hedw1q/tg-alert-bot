package ru.hedw1q.TgBot.tg;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class TgBot extends TelegramLongPollingCommandBot {
    private static final Logger logger = LoggerFactory.getLogger(TgBot.class);
    private static final Map<String, String> getenv = System.getenv();



    private String BOT_NAME=getenv.get("telegram.botname");
    private String BOT_TOKEN=getenv.get("telegram.bottoken");

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public void processNonCommandUpdate(Update update){
//        Message msg = update.getMessage();
//        Long chatId = msg.getChatId();
//        String userName = getUserName(msg);
//        String answer = "Привет! ты милый";
//        setAnswer(chatId, userName, answer);
    }

    public void sendMessageToChannel(Long chatId, String text){
        setAnswer(chatId,"",text);
    }

    /** Отправка ответа
     * @param chatId id чата
     * @param userName имя пользователя
     * @param text текст ответа
     */
     private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
         try {
             execute(answer);
         } catch (TelegramApiException e) {
             logger.error(e.getStackTrace().toString());
         }
     }

    /**
     * Формирование имени пользователя
     *
     * @param msg сообщение
     */
    private String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}

