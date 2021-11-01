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

public abstract class BaseTwitchStreamer implements BaseStreamerI {

    public static long TG_CHANNEL_ID = -1001537091172L;
    protected static long AUDIT_TG_CHANNEL_ID = 890471143L;

    protected static final Logger logger = LoggerFactory.getLogger(BaseTwitchStreamer.class);

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

    public BaseTwitchStreamer() {
    }

    public BaseTwitchStreamer(String channelName, AuthData authData) {
        this.channelName = channelName;

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
    void afterInit() {
    }

    @PreDestroy
    void beforeDestroy() {
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

    public <T> void registerNewEvent(Class<T> eventClass, Consumer<T> consumer) {
        twitchClient.getEventManager().onEvent(eventClass, consumer);
    }

    @Override
    public void onChannelSubscriptionEvent(SubscriptionEvent event) {
    }

    @Override
    public void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        Stream newStream = new Stream(channelGoLiveEvent.getChannel().getName(), LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC), "Twitch");

        String message = "❗️Поток от " + channelName + " на Twitch ❗️\n" +
                "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                "\n" +
                "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

        String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);

        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(newStream.getStreamStartTime().toInstant(ZoneOffset.UTC), channelGoLiveEvent.getChannel().getName(), "Twitch");
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            audit(tgBot, e);
        } finally {
            channelViewerCount = 0;
        }
    }

    @Override
    public void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent channelViewerCountUpdateEvent) {
        channelViewerCount = channelViewerCountUpdateEvent.getViewerCount();
    }

    @Override
    public void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        Duration streamDuration;
        Integer streamId = null;
        Stream finishedStream = streamService.getCurrentLiveStreamByChannelName(channelGoOfflineEvent.getChannel().getName());
        try {
            streamId = finishedStream.getId();
            streamDuration = Duration.between(finishedStream.getStreamStartTime(),
                    LocalDateTime.ofInstant(channelGoOfflineEvent.getFiredAtInstant(), ZoneOffset.UTC));
        } catch (Exception e) {
            streamDuration = Duration.ZERO;
            audit(tgBot, e);
        }
        try {
            String message = "Стрим <a href=\"https://twitch.tv/" + channelName + "\">" + channelName + "</a> на Twitch окончен\n" +
                    "Длительность: " + streamDuration.toHours() + " ч. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " мин.\n" +
                    "Зрителей: " + channelViewerCount;

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message, true);

            streamService.setStreamOfflineById(channelGoOfflineEvent.getFiredAtInstant(), streamId);
        } catch (Exception e) {
            audit(tgBot, e);
        } finally {
            channelViewerCount = 0;
        }
    }

    @Override
    public void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent) {

        String message = "❗️" + channelName + " сменил/a игру на стриме ❗️\n" +
                "Категория: " + channelChangeGameEvent.getStream().getGameName() + "\n" +
                "Название: " + channelChangeGameEvent.getStream().getTitle();

        String thumbnailUrl = channelChangeGameEvent.getStream().getThumbnailUrl(1600, 900);

        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            audit(tgBot, e);
        }
    }

    public static void audit(TgBot tgBot, Exception exception) {
        tgBot.sendTextMessageToChannel(AUDIT_TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(exception));
        logger.error(ExceptionUtils.getFullStackTrace(exception));
    }

    public static void audit(TgBot tgBot, Logger logger, Exception exception) {
        tgBot.sendTextMessageToChannel(AUDIT_TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(exception));
        logger.error(ExceptionUtils.getFullStackTrace(exception));
    }

    public static void audit(TgBot tgBot, String msg) {
        tgBot.sendTextMessageToChannel(AUDIT_TG_CHANNEL_ID, msg);
        logger.info(msg);
    }
}

