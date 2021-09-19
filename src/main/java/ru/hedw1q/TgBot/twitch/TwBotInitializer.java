package ru.hedw1q.TgBot.twitch;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.twitch.config.TwitchConfiguration;
import ru.hedw1q.TgBot.twitch.config.AuthData;

import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class TwBotInitializer {
    private static final Map<String, String> getenv = System.getenv();

    static {
        AuthData twitchAuth = new AuthData(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"));
    }


    @Bean(name = "honeyramonaflowers")
    public TwBot createBotHoneyramonaflowers() {
        TwitchConfiguration twitchConfiguration = new TwitchConfiguration(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"),
                "honeyramonaflowers",
                "Anti-FUN"
        );
        return TwBot.create(twitchConfiguration);
    }

    @Bean(name = "krabick")
    public TwBot createBotKrabick() {
        TwitchConfiguration twitchConfigurationKrab = new TwitchConfiguration(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"),
                "Krabick",
                "Anti-FUN"
        );
        return TwBot.create(twitchConfigurationKrab);
    }

}
