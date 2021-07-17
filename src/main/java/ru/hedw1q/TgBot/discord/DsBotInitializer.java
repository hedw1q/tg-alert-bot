package ru.hedw1q.TgBot.discord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.discord.events.EventListener;

import java.util.HashMap;
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
    public static final Map<String, String> stickerMap = new HashMap<>();

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
        initializeStickers();
        logger.info("Connected to discord channels");
        return client;
    }

    private void initializeStickers(){
        stickerMap.put("WeirdChamp", "\uD83D\uDE10");
        stickerMap.put("honeyr1WOW", "\uD83D\uDE0A");
        stickerMap.put("ZdarovaEZ", "\uD83D\uDE0E"); //ez
        stickerMap.put("krabicPerv", "\uD83E\uDD14");//thinkies
        stickerMap.put("honeyr13HEAD", "\uD83D\uDE02");//laughing
        stickerMap.put("honeyOmega", "\uD83D\uDE02");
        stickerMap.put("honeyr1Panic", "\uD83D\uDE31");
        stickerMap.put("peepoBlush", "☺");
        stickerMap.put("peepoClown", "\uD83E\uDD21");
        stickerMap.put("PepeChill", "\uD83D\uDE0E");
        stickerMap.put("Pepechill", "\uD83D\uDE0E");
        stickerMap.put("HappyCat", "\uD83D\uDE38");
        stickerMap.put("Pog", "\uD83D\uDE32");
        stickerMap.put("SadCat", "\uD83D\uDE3F");
        stickerMap.put("PeepoPat", "☺");
        stickerMap.put("5Head", "\uD83E\uDDD0");
        stickerMap.put("PepeHands", "\uD83D\uDE25");
        stickerMap.put("honeyPuf", "\uD83D\uDE3E");
        stickerMap.put("peepoLove", "❤"); //love
        stickerMap.put("KEKW", "\uD83D\uDE02");
        stickerMap.put("3Head", "\uD83E\uDD13");
        stickerMap.put("zloopPooPoo", "\uD83D\uDE42");
        stickerMap.put("billyReady", "\uD83D\uDE0F");
        stickerMap.put("NeDovolen", "\uD83D\uDE15");
        stickerMap.put("Dovolen", "\uD83D\uDE42");
        stickerMap.put("Rasstroen", "☹");
        stickerMap.put("AnimeHeart", "❤");
        stickerMap.put("Kapp","( ͡° ͜ʖ ͡°)");
        stickerMap.put("Kappa","( ͡° ͜ʖ ͡°)");
        stickerMap.put("PepoG","\uD83E\uDDD0");
        stickerMap.put("Psyduck","\uD83D\uDE38");
        stickerMap.put("PeepoHmm","\uD83E\uDD14");
        stickerMap.put("ozonF","\uD83D\uDE36");
        stickerMap.put("PepoThink","\uD83E\uDD14");
        stickerMap.put("FeelsSpecialMan","\uD83E\uDD24");
        stickerMap.put("AnimeWink","\uD83D\uDE0A");
        stickerMap.put("Gun","\uD83D\uDD2B");
        stickerMap.put("1_","\uD83C\uDDEB");
        stickerMap.put("PepeLaugh","\uD83D\uDE06");
        stickerMap.put("TopPika","\uD83D\uDE0A");
        stickerMap.put("AnimeGasm","\uD83D\uDE0A");
        stickerMap.put("AnimeHey","\uD83D\uDE0A");
        stickerMap.put("peepoMad","\uD83D\uDE21"); //rage
        stickerMap.put("410","\uD83D\uDE0A");
        stickerMap.put("honeyr1Plak","\uD83D\uDE25");
        stickerMap.put("PoofEZ","\uD83D\uDE0E");
        stickerMap.put("honeySleeper","\uD83D\uDE34");
        stickerMap.put("honeyr1D","\uD83D\uDE3C");
        stickerMap.put("honeyr1Cry","\uD83D\uDE3F");
        stickerMap.put("honeyr1Ezy","\uD83D\uDE0E");
    }
}
