package ru.hedw1q.TgBot.telegram.commands;

import lombok.SneakyThrows;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hedw1q.TgBot.twitch.entities.streamers.Ramona;

/**
 * @author hedw1q
 */
@Component
public class SettingsCommand extends BotCommand {

    private static final Logger logger = LoggerFactory.getLogger(SettingsCommand.class);

    @Autowired
    Ramona ramona;

    public SettingsCommand() {
        super("switch", "Поменять саб эвент");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        StringBuilder response = new StringBuilder("honeyramonaflowers sub event status ");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat.getId().toString());

        try {
            if(ramona.isSubEnabled()){
                response.append("disabled");
                ramona.setSubEnabled(false);
            }else{
                response.append("enabled");
                ramona.setSubEnabled(true);
            }

            sendMessage.setText(response.toString());
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
