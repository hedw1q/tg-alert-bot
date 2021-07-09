package ru.hedw1q.TgBot.ds.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author hedw1q
 */
@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    private static final long TG_CHANNEL_ID = -1001537091172L;
    private static final long DS_CHANNEL_ID=860904072306229288L;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        if(event.getMessage().getChannelId().asLong()!=DS_CHANNEL_ID){
            return Mono.empty();
        }
        logger.info("From:{}; {}", event.getMessage().getAuthor().get().getUsername(), event.getMessage().getContent());
        tgBot.sendMessageToChannel(TG_CHANNEL_ID, event.getMessage().getContent());

        return Mono.empty();
    }
}