package ru.hedw1q.TgBot.tw;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.tw.config.TwitchConfiguration;

import java.util.Map;

/**
 * @author hedw1q
 */
@Component
public class TwBotInitializer {

    private static final Map<String, String> getenv = System.getenv();

    @Bean
    public TwBot createBot() {
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



}
