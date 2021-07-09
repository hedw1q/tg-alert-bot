package ru.hedw1q.TgBot.ds;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.ds.events.EventListener;

import java.util.List;
import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class DsBotInitializer {
    private static final Map<String, String> getenv = System.getenv();

    private String DS_TOKEN=getenv.get("discord.token");
    private static final Logger logger = LoggerFactory.getLogger(DsBotInitializer.class);

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = DiscordClientBuilder.create(DS_TOKEN)
                .build()
                .login()
                .block();

        for (EventListener<T> listener : eventListeners) {
            client.on(listener.getEventType())
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe();
        }

        logger.info("Connected to discord channel");
        return client;
    }
}
