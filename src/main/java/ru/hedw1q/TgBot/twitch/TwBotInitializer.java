package ru.hedw1q.TgBot.twitch;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.twitch.config.TwitchConfiguration;

import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class TwBotInitializer {
    private static final Map<String, String> getenv = System.getenv();

    @Bean(name="honeyramonaflowers")
    public TwBot createBotHoneyramonaflowers() {
        TwitchConfiguration twitchConfiguration=new TwitchConfiguration(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"),
                "Pufozavr",
                "honeyramonaflowers",
                "Anti-FUN"
        );
        return TwBot.create(twitchConfiguration);
    }

    @Bean(name="Krabick")
    public TwBot createBotKrabick() {
        TwitchConfiguration twitchConfigurationKrab=new TwitchConfiguration(
                getenv.get("twitch.oAuthToken"),
                getenv.get("twitch.clientId"),
                getenv.get("twitch.clientSecret"),
                "Pufozavr",
                "Krabick",
                "Anti-FUN"
        );
        return TwBot.create(twitchConfigurationKrab);
    }
}
