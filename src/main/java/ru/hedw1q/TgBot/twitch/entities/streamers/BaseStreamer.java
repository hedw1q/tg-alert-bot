package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.services.StreamService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author hedw1q
 */

public abstract class BaseStreamer {

    public static long TG_CHANNEL_ID = -1001537091172L;

    protected static final Logger logger = LoggerFactory.getLogger(BaseStreamer.class);

    @Getter
    @Setter
    String channelName;
    @Getter
    private String channelId;
    @Autowired
    public TgBot tgBot;
    @Autowired
    public StreamService streamService;

    protected int channelViewerCount;

    TwitchChat twitchChat;
    TwitchClient twitchClient;

    public BaseStreamer() { }

    public BaseStreamer(String channelName, AuthData authData) {
        this.channelName = channelName;
        logger.info(tgBot.getBotUsername());
        OAuth2Credential credential = new OAuth2Credential("twitch", authData.getOAuthToken());

        twitchClient = TwitchClientBuilder.builder()
                .withClientId(authData.getClientId())
                .withClientSecret(authData.getClientSecret())
                .withEnableChat(true)
                .withEnableHelix(true)
                .withChatAccount(credential)
                .build();

        twitchClient.getClientHelper()
                .enableStreamEventListener(channelName);

        channelId = twitchClient.getHelix().getUsers(null, null, Collections.singletonList(channelName))
                .execute()
                .getUsers()
                .get(0)
                .getId();

        registerEventHandlers(twitchClient.getEventManager());

        twitchChat = twitchClient.getChat();
        twitchChat.joinChannel(channelName);

        logger.info("Connected to twitch.tv/{}, channelId={}", channelName, channelId);
    }

    @PostConstruct
    void construct() {
    }
    @PreDestroy
    void preDestroy(){
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
        eventManager.
                onEvent(SubscriptionEvent.class, this::onChannelSubscriptionEvent);
    }

    public <T> void registerNewEvent(Class<T> eventClass, Consumer<T> consumer){
        twitchClient.getEventManager().onEvent(eventClass, consumer);
    }

    protected void onChannelSubscriptionEvent(SubscriptionEvent event) { }

    protected void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        Stream newStream = new Stream(channelGoLiveEvent.getChannel().getName(), LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC));

        String message = "❗️Поток от " + channelGoLiveEvent.getChannel().getName() + " на Twitch ❗️\n" +
                "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                "\n" +
                "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

        String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);

        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(newStream.getStreamStartTime().toInstant(ZoneOffset.UTC), channelGoLiveEvent.getChannel().getName());
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            newStream = null;
            channelViewerCount = 0;
        }
    }

    protected void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent channelViewerCountUpdateEvent) {
        channelViewerCount = channelViewerCountUpdateEvent.getViewerCount();
    }

    protected void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        tgBot.sendTextMessageToChannel(890471143L, channelGoOfflineEvent.toString());
        Duration streamDuration;
        Integer streamId = null;
        Stream finishedStream = streamService.getLastStreamByChannelName(channelGoOfflineEvent.getChannel().getName());
        try {
            streamId = finishedStream.getId();
            finishedStream.setStreamFinishTime(LocalDateTime.ofInstant(channelGoOfflineEvent.getFiredAtInstant(), ZoneOffset.UTC));
            streamDuration = Duration.between(finishedStream.getStreamStartTime().toInstant(ZoneOffset.UTC), finishedStream.getStreamFinishTime());
        } catch (Exception e) {
            streamDuration = Duration.ZERO;
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        try {
            String message = "⚫️ Стрим на Twitch окончен ⚫️ \n" +
                    "Длительность: " + streamDuration.toHours() + " ч. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " мин.\n" +
                    "Зрителей: " + channelViewerCount + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelGoOfflineEvent.getChannel().getName();

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);

            streamService.setStreamOfflineById(finishedStream.getStreamFinishTime().toInstant(ZoneOffset.UTC), streamId);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            finishedStream = null;
            channelViewerCount = 0;
        }
    }

    protected void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent) {

        String message = "❗️" + channelChangeGameEvent.getChannel().getName() + " сменил/a игру на стриме ❗️\n" +
                "Категория: " + channelChangeGameEvent.getStream().getGameName() + "\n" +
                "Название: " + channelChangeGameEvent.getStream().getTitle();

        String thumbnailUrl = channelChangeGameEvent.getStream().getThumbnailUrl(1600, 900);

        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}

