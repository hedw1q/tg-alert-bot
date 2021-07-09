package ru.hedw1q.TgBot.ds.events;

import discord4j.core.event.domain.message.MessageUpdateEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author hedw1q
 */
@Service
public class MessageUpdateListener extends MessageListener implements EventListener<MessageUpdateEvent> {

    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
            return Mono.empty();
//            return Mono.just(event)
//                    .filter(MessageUpdateEvent::isContentChanged)
//                    .flatMap(MessageUpdateEvent::getMessage)
//                    .flatMap(super::processCommand);

    }
}
