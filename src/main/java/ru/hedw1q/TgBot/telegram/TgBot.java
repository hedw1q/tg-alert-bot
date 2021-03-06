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
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
//
//        sendTextMessageToChannel(chatId,chatId.toString());
    }

    public Message updateMessage(Message oldMessage ,String newText){
        oldMessage.setText(newText);
        return oldMessage;
    }

    public Message sendTextMessageToChannel(Long chatId, String text) {
        return setAnswer(chatId, text);
    }

    public Message sendTextMessageToChannel(Long chatId, String text, boolean disablePreview) {
        return setAnswer(chatId, text,disablePreview);
    }

    public Message sendAttachmentMessageToChannel(Long chatId, String url, String text) {
        try {
            URL urlObject = new URL(url);
            URLConnection conn = urlObject.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
            conn.connect();
            String contentType = conn.getContentType().trim().split(";")[0];
//           final  Pattern pattern = Pattern.compile("<@.+>");
//            final Matcher matcher = pattern.matcher(contentType);
       //     File file = new File(urlObject.getFile());
           // FileUtils.copyInputStreamToFile(conn.getInputStream(), file);

                switch (contentType) {
                    case "image/png":
                    case "image/bmp":
                    case "image/tiff":
                    case "image/jpeg":
                        return sendImageMessageToChannel(chatId, conn.getInputStream(), text);
                    case "image/gif":
                        return sendAnimationMessageToChannel(chatId, conn.getInputStream(), text);
                    case "video/mpeg":
                    case "video/mp4":
                    case "video/ogg":
                    case "video/quicktime":
                    case "video/webm":
                    case "video/x-ms-wmv":
                    case "video/x-flv":
                    case "video/x-msvideo":
                        return sendAnimationMessageToChannel(chatId, conn.getInputStream(),text);
                    default:
                        return null;
                }
        }catch (MalformedURLException urlException){
            sendTextMessageToChannel(chatId,text);
            logger.warn(ExceptionUtils.getFullStackTrace(urlException));
            return null;
        }catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
            return null;
        }
    }

    private Message sendImageMessageToChannel(Long chatId, InputStream inputStream, String text) throws TelegramApiException {
        SendPhoto msg = new SendPhoto();
        msg.setPhoto(new InputFile(inputStream, "image"));
        msg.setChatId(chatId.toString());
        msg.setCaption(text);
       return execute(msg);
    }

    private Message sendAnimationMessageToChannel(Long chatId, InputStream inputStream, String text) throws TelegramApiException {
        SendVideo msg = new SendVideo();
        msg.setVideo(new InputFile(inputStream, "video"));
        msg.setChatId(chatId.toString());
        msg.setCaption(text);
        return execute(msg);
    }

    /**
     * ???????????????? ????????????
     *
     * @param chatId id ????????
     * @param text   ?????????? ????????????
     */
    private Message setAnswer(Long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setParseMode("HTML");
        try {
           return execute(answer);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * ???????????????? ????????????
     *
     * @param chatId id ????????
     * @param text   ?????????? ????????????
     * @param disablePreview - ??????????????????????????????????? ???????????? ????????????
     */
    private Message setAnswer(Long chatId, String text, boolean disablePreview) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setParseMode("HTML");
        answer.setDisableWebPagePreview(disablePreview);
        try {
            return execute(answer);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * ???????????????????????? ?????????? ????????????????????????
     *
     * @param msg ??????????????????
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

