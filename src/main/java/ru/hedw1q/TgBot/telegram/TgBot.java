package ru.hedw1q.TgBot.telegram;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class TgBot extends TelegramLongPollingCommandBot {
    private static final Logger logger = LoggerFactory.getLogger(TgBot.class);
    private static final Map<String, String> getenv = System.getenv();


    private String BOT_NAME = getenv.get("telegram.botname");
    private String BOT_TOKEN = getenv.get("telegram.bottoken");

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
//        Message msg = update.getMessage();
//        Long chatId = msg.getChatId();
//        String answer = "Привет! ты милый";
//        String url="https://cdn.discordapp.com/attachments/552454831122546699/810226816286982154/image0.jpg";
//        sendAttachmentMessageToChannel(chatId,url);
    }

    public void sendTextMessageToChannel(Long chatId, String text) {
        setAnswer(chatId, text);
    }

    public void sendAttachmentMessageToChannel(Long chatId, String url, String text) {
        try {
            URL urlObject = new URL(url);
            URLConnection conn = urlObject.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
            conn.connect();
            String contentType = conn.getContentType().trim();
            File file = new File(urlObject.getFile());
            FileUtils.copyInputStreamToFile(conn.getInputStream(), file);
            switch (contentType) {
                case "image/png":
                case "image/bmp":
                case "image/tiff":
                case "image/jpeg":
                    sendImageMessageToChannel(chatId, file, text);
                case "image/gif":
                    sendAnimationMessageToChannel(chatId, file, text);
                default:
                    return;
            }
        } catch (TelegramApiException | IOException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }

    private void sendImageMessageToChannel(Long chatId, File file, String text) throws TelegramApiException {
        SendPhoto msg = new SendPhoto();
        msg.setPhoto(new InputFile(file));
        msg.setChatId(chatId.toString());
        msg.setCaption(text);

        execute(msg);
    }

    private void sendAnimationMessageToChannel(Long chatId, File file, String text) throws TelegramApiException {
        SendAnimation msg = new SendAnimation();
        msg.setAnimation(new InputFile(file));
        msg.setChatId(chatId.toString());
        msg.setCaption(text);

        execute(msg);
    }

    /**
     * Отправка ответа
     *
     * @param chatId id чата
     * @param text   текст ответа
     */
    private void setAnswer(Long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
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

