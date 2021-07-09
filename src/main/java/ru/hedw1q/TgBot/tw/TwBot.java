package ru.hedw1q.TgBot.tw;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.tg.TgBot;
import ru.hedw1q.TgBot.tw.config.TwitchConfiguration;

/**
 * @author hedw1q
 */
@Component
public class TwBot {

    @Autowired
    private TgBot tgBot;
    private static final long TG_CHANNEL_ID = -1001537091172L;
    private static final Logger logger = LoggerFactory.getLogger(TwBot.class);
    private TwitchChat twitchChat;

    public static TwBot create(TwitchConfiguration twitchConfiguration) {
        TwBot twBot = new TwBot();
        twBot.connect(twitchConfiguration);
        return twBot;
    }

     void connect(TwitchConfiguration twitchConfiguration) {
        TwitchClient twitchClient = createTwitchClient(twitchConfiguration);
        registerEventHandlers(twitchClient.getEventManager());

        twitchChat = twitchClient.getChat();
        twitchChat.joinChannel(twitchConfiguration.getChannelName());

         logger.info("Connected to twitch.tv/"+ twitchConfiguration.getChannelName());
     }


    private TwitchClient createTwitchClient(TwitchConfiguration twitchConfiguration) {

        OAuth2Credential credential = new OAuth2Credential("twitch", twitchConfiguration.getOAuthToken());

        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withClientId(twitchConfiguration.getClientId())
                .withClientSecret(twitchConfiguration.getClientSecret())
                .withEnableChat(true)
                .withEnableHelix(true)
                .withEnableKraken(true)
                .withChatAccount(credential)
                .withCommandTrigger("!")
                .build();

        twitchClient.getClientHelper()
                .enableStreamEventListener(twitchConfiguration.getChannelName());

        return twitchClient;
    }


    private void registerEventHandlers(EventManager eventManager) {
        eventManager
                .onEvent(ChannelGoLiveEvent.class, this::onChannelGoLive);
        eventManager.
                onEvent(ChannelGoOfflineEvent.class, this::onChannelGoOffline);
//        eventManager
//                .onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    private void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        tgBot.sendMessageToChannel(TG_CHANNEL_ID, "Хуня завела! Pog");
    }

    private void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        tgBot.sendMessageToChannel(TG_CHANNEL_ID, "Стрим закончен! SadCat");
    }

    private void onChannelMessage(ChannelMessageEvent channelMessageEvent){
        tgBot.sendMessageToChannel(TG_CHANNEL_ID, channelMessageEvent.getMessage());
    }
}
