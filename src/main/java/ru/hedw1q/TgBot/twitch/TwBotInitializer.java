package ru.hedw1q.TgBot.twitch;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.TwitchChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.streamers.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
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

    @Bean
    public MaleStreamer initKrabickBot() {
        return new MaleStreamer("Krabick", twitchAuth);
    }

    @Bean
    public Ramona initRamonaBot() {
        return new Ramona("honeyramonaflowers", twitchAuth);
    }


}
