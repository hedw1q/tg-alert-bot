package ru.hedw1q.TgBot.ds.events;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import ru.hedw1q.TgBot.tg.TgBot;


/**
 * @author hedw1q
 */

public abstract class MessageListener {

    @Autowired
    protected TgBot tgBot;

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    public Mono<Void> processCommand(Message eventMessage) {
        return Mono.empty();
    }
}