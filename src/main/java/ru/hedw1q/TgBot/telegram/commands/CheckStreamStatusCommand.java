package ru.hedw1q.TgBot.telegram.commands;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author hedw1q
 */
public class CheckStreamStatusCommand extends BotCommand {

    private static final Logger logger = LoggerFactory.getLogger(CheckStreamStatusCommand.class);

    public CheckStreamStatusCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        try{
            SendMessage sendMessage=new SendMessage();
            sendMessage.setText("Да живой я, ацтань");
            sendMessage.setChatId(chat.getId().toString());

            SendSticker sendSticker=new SendSticker();
            sendSticker.setSticker(new InputFile("CAACAgIAAxkBAAEClDdg7zJxETfRjQK8Y66XZraozz1-8gACFgADx8k_FtZi2DrThc88IAQ"));
            sendSticker.setChatId(chat.getId().toString());

            absSender.execute(sendMessage);
            absSender.execute(sendSticker);

            System.gc();
        } catch (TelegramApiException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
