package ru.hedw1q.TgBot.twitch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.streamers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
//    public BaseTwitchStreamer initKrabickBot() {
//        return new MaleTwitchStreamer("Krabick", twitchAuth);
//    }

    @Bean
    @Qualifier("ramona")
    public Ramona initRamona() {
        return new Ramona("honeyramonaflowers", twitchAuth);
    }

//    @Bean
//    public MaleTwitchStreamer initArtem() {
//        return new MaleTwitchStreamer("Art_em322", twitchAuth);
//    }

//    @Bean
//    public MaleTwitchStreamer initIlnur() {
//        return new MaleTwitchStreamer("yolopopa", twitchAuth);
//    }
//
//    @Bean
//    public MaleTwitchStreamer initNikita() {
//        return new MaleTwitchStreamer("1loveanother", twitchAuth);
//    }

    @Bean
    public Zanuda initZnd() {
        return new Zanuda("zanuda", twitchAuth);
    }

    public static AuthData getAuthData() {
        return twitchAuth;
    }
}
