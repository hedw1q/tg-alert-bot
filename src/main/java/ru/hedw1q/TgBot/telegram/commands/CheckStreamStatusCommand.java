package ru.hedw1q.TgBot.telegram.commands;


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
import ru.hedw1q.TgBot.twitch.entities.Streamer;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.hedw1q.TgBot.twitch.services.StreamerService;

import java.util.List;

/**
 * @author hedw1q
 */
@Component
public class CheckStreamStatusCommand extends BotCommand {

    private static final Logger logger = LoggerFactory.getLogger(CheckStreamStatusCommand.class);

    @Autowired
    StreamerService streamerService;
    @Autowired
    StreamService streamService;

    public CheckStreamStatusCommand() {
        super("status", "Статус стримов");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        List<Streamer> streamers=streamerService.getStreamers();
        try {
            SendMessage sendMessage = new SendMessage();
             StringBuilder msg =new StringBuilder();
            for (Streamer streamer: streamers) {
                msg.append(streamer.getChannelName());
                msg.append(":");
                msg.append(streamService.getStreamStatusByChannelName(streamer.getChannelName()));
                msg.append(";\n");
            }

            sendMessage.setText(msg.toString());
            sendMessage.setChatId(chat.getId().toString());

            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
