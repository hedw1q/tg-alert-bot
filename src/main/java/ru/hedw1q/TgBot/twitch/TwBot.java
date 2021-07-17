package ru.hedw1q.TgBot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.config.TwitchConfiguration;

import java.time.Duration;
import java.time.Instant;

/**
 * @author hedw1q
 */
@Component
public class TwBot {
    public static long TG_CHANNEL_ID = -1001537091172L;

    @Autowired
    private TgBot tgBot;
    private static final Logger logger = LoggerFactory.getLogger(TwBot.class);
    private TwitchChat twitchChat;
    private Instant streamStartTime;
    private Instant streamFinishTime;
    private int channelViewerCount;

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

        logger.info("Connected to twitch.tv/" + twitchConfiguration.getChannelName());
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
                .build();

        twitchClient.getClientHelper()
                .enableStreamEventListener(twitchConfiguration.getChannelName());

        return twitchClient;
    }


    void registerEventHandlers(EventManager eventManager) {
        eventManager
                .onEvent(ChannelGoLiveEvent.class, this::onChannelGoLive);
        eventManager.
                onEvent(ChannelGoOfflineEvent.class, this::onChannelGoOffline);
        eventManager.
                onEvent(ChannelViewerCountUpdateEvent.class, this::onChannelViewerCountUpdate);
//        eventManager
//                .onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        logger.info(channelGoLiveEvent.getChannel().getName()+" alive");

        streamStartTime=channelGoLiveEvent.getStream().getStartedAtInstant();
        String message = "❗️ "+channelGoLiveEvent.getChannel().getName()+" завел на Twitch  ❗️\n" +
                "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                "\n" +
                "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

        String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl();

        tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        //tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
    }

    void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent channelViewerCountUpdateEvent) {
        logger.info(channelViewerCountUpdateEvent.getChannel().getName()+":new viewer count="+
                +channelViewerCountUpdateEvent.getViewerCount());
        channelViewerCount = channelViewerCountUpdateEvent.getViewerCount();
    }

    void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        logger.info(channelGoOfflineEvent.getChannel().getName()+" offline");
        try {
            streamFinishTime=channelGoOfflineEvent.getFiredAtInstant();
            Duration streamDuration = Duration.between(streamStartTime, streamStartTime);
            String message = "⚫️ Стрим на Twitch окончен ⚫️ \n" +
                    "Длительность: " + streamDuration.toHours() + " ч. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " мин.\n" +
                    "Зрителей: " + channelViewerCount + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelGoOfflineEvent.getChannel().getName();

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
        }finally {
            streamStartTime=null;
            streamFinishTime=null;
            channelViewerCount=0;
            System.gc();
        }
    }

//    private void onChannelMessage(ChannelMessageEvent channelMessageEvent){
//        tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, channelMessageEvent.getMessage());
//    }
}
