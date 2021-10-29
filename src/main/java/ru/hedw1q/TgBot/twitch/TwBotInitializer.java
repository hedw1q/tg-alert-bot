package ru.hedw1q.TgBot.twitch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.streamers.*;

import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class TwBotInitializer {

    private static final Map<String, String> getenv = System.getenv();

    private static final AuthData twitchAuth;

    static {
        twitchAuth = new AuthData(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"));
    }

//    @Bean
//    @Qualifier("krabick")
//    public Krabick initKrabickBot() {
//        return new Krabick("Krabick", twitchAuth);
//    }

    @Bean
    @Qualifier("ramona")
    public BaseTwitchStreamer initRamonaBot() {
        return new Ramona("honeyramonaflowers", twitchAuth);
    }

    @Bean
    @Qualifier("ellanta")
    public BaseTwitchStreamer initEllantaBot(){ return new FemaleTwitchStreamer("ellanta", twitchAuth); }

    public static AuthData getAuthData() {
        return twitchAuth;
    }
}
