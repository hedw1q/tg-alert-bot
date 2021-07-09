package ru.hedw1q.TgBot.ds.events;

import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * @author hedw1q
 */
public interface EventListener<T extends Event> {

    static final Logger logger = LoggerFactory.getLogger(EventListener.class);

    Class<T> getEventType();
    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        logger.error("Unable to process " + getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}