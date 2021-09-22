package ru.hedw1q.TgBot.telegram.commands;


import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author hedw1q
 */
@Deprecated
public class CheckStreamStatusCommand extends BotCommand {

    private static final Logger logger = LoggerFactory.getLogger(CheckStreamStatusCommand.class);

    public CheckStreamStatusCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        try {
            SendMessage sendMessage = new SendMessage();
             String msg = "test";

            sendMessage.setText(msg);
            sendMessage.setChatId(chat.getId().toString());

            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
