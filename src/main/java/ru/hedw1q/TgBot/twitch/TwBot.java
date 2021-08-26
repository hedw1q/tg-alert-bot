package ru.hedw1q.TgBot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import com.github.twitch4j.helix.domain.UserList;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.config.TwitchConfiguration;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.services.StreamService;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * @author hedw1q
 */
public class TwBot {
    public static long TG_CHANNEL_ID = -1001537091172L;

    @Autowired
    private TgBot tgBot;
    @Autowired
    StreamService streamService;
    private static final Logger logger = LoggerFactory.getLogger(TwBot.class);
    private TwitchClient twitchClient;
    private TwitchChat twitchChat;
    private Instant streamStartTime;
    private Instant streamFinishTime;
    private int channelViewerCount;
    private String channelId;

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

        twitchClient = TwitchClientBuilder.builder()
                .withClientId(twitchConfiguration.getClientId())
                .withClientSecret(twitchConfiguration.getClientSecret())
                .withEnableChat(true)
                .withEnableHelix(true)
                .withChatAccount(credential)
                .build();

        twitchClient.getClientHelper()
                .enableStreamEventListener(twitchConfiguration.getChannelName());

        channelId = twitchClient.getHelix().getUsers(null, null, Arrays.asList(twitchConfiguration.getChannelName()))
                .execute()
                .getUsers()
                .get(0)
                .getId();


        return twitchClient;
    }

    @Deprecated
    public String getStreamIsAlive() {
        if (twitchClient.getHelix().getStreams(null, "", "", null, null, null, Arrays.asList(channelId), null)
                .execute().getStreams().get(0).getType().equals("live"))
            return "live";
        return "offline";
    }

    void registerEventHandlers(EventManager eventManager) {
        eventManager
                .onEvent(ChannelGoLiveEvent.class, this::onChannelGoLive);
        eventManager.
                onEvent(ChannelGoOfflineEvent.class, this::onChannelGoOffline);
        eventManager.
                onEvent(ChannelViewerCountUpdateEvent.class, this::onChannelViewerCountUpdate);
        eventManager.
                onEvent(ChannelChangeGameEvent.class, this::onChannelChangeGame);
//        eventManager
//                .onEvent(ChannelMessageEvent.class, this::onChannelMessage);
    }

    void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        streamStartTime = channelGoLiveEvent.getStream().getStartedAtInstant();
        try {
            String message = "❗️Поток от " + channelGoLiveEvent.getChannel().getName() + " на Twitch  ❗️\n" +
                    "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                    "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

            String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(320, 180);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(streamStartTime,channelGoLiveEvent.getChannel().getName());
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(e));
            //     logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            streamStartTime=null;
            streamFinishTime = null;
            channelViewerCount = 0;
        }
    }

    void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent channelViewerCountUpdateEvent) {
        channelViewerCount = channelViewerCountUpdateEvent.getViewerCount();
    }

    void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        Duration streamDuration;
        Integer streamId=null;
        try {
            Stream stream=streamService.getLastStreamByChannelName(channelGoOfflineEvent.getChannel().getName());
            streamId=stream.getId();
            streamFinishTime = channelGoOfflineEvent.getFiredAtInstant();
            streamDuration = Duration.between(stream.getStreamStartTime(), streamFinishTime);
        } catch (NullPointerException | SQLException e) {
            streamDuration = Duration.ZERO;
        }
        try {
            String message = "⚫️ Стрим на Twitch окончен ⚫️ \n" +
                    "Длительность: " + streamDuration.toHours() + " ч. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " мин.\n" +
                    "Зрителей: " + channelViewerCount + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelGoOfflineEvent.getChannel().getName();

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            streamService.setStreamOfflineById(streamFinishTime, streamId);
        } catch (Exception e) {
              logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            streamStartTime = null;
            streamFinishTime = null;
            channelViewerCount = 0;
        }
    }

    void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent) {
        try {
            String message = "❗️"+channelChangeGameEvent.getChannel().getName() + " сменил игру на стриме  ❗️\n" +
                    "Категория: " + channelChangeGameEvent.getStream().getGameName() + "\n" +
                    "Зрителей: " + channelChangeGameEvent.getStream().getViewerCount() + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelChangeGameEvent.getChannel().getName();

            String thumbnailUrl = channelChangeGameEvent.getStream().getThumbnailUrl(320, 180);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(e));
        }
    }

//    private void onChannelMessage(ChannelMessageEvent channelMessageEvent){
//        tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, channelMessageEvent.getMessage());
//    }
}
