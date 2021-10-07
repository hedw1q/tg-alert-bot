package ru.hedw1q.TgBot.goodgame;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.StreamStatus;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.maximkulikov.goodgame.api.GoodGame;
import ru.maximkulikov.goodgame.api.handlers.StreamChannelResponseHandler;
import ru.maximkulikov.goodgame.api.models.ChannelContainer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
@Component
@EnableAsync
public class GGBot {
    public static final Long AUDIT_TG_CHANNEL_ID = 890471143L;
    public static Long TG_CHANNEL_ID = -1001537091172L;
    public static String GG_CHANNEL_NAME = "KinoKrabick";
    private final Logger logger = LoggerFactory.getLogger(GGBot.class);

    @Autowired
    StreamService streamService;
    @Autowired
    TgBot tgBot;
    GoodGame gg = new GoodGame();

    StreamStatus streamStatus = StreamStatus.OFFLINE;


    @Scheduled(fixedRate = 60000)
    @Async
    public void handle() {
        gg.streams().getChannel(GG_CHANNEL_NAME, new StreamChannelResponseHandler() {

            @Override
            public void onFailure(int i, String s, String s1) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }

            @Override
            public void onSuccess(ChannelContainer channelContainer) {
                //broadcast_started=1632650400
                streamStatus = strToStreamStatus(channelContainer.getStatus());
                Stream currentStream = streamService.getLastStreamByChannelName(GG_CHANNEL_NAME);

                if (streamStatus.equals(StreamStatus.LIVE) && currentStream == null) {
                    onChannelGoLive(channelContainer);
                }
                else if (streamStatus.equals(StreamStatus.OFFLINE) && currentStream != null) {
                    try {
                        onChannelGoOffline(channelContainer, currentStream);
                    }finally {
                        currentStream=null;
                    }
                }
            }
        });
    }

    void onChannelGoLive(ChannelContainer channelContainer){
        String message = "❗️Крабик завел на GoodGame ❗️\n" +
                "Название: " + channelContainer.getChannel().getTitle() + "\n" +
                "Категория: " + channelContainer.getChannel().getGames().get(0).getTitle() + "\n" +
                "\n" +
                "Ссылка: https://goodgame.ru/channel/"+GG_CHANNEL_NAME;
        try {
            String thumbnailUrl = "https:" + channelContainer.getChannel().getThumb();

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(Instant.now(), GG_CHANNEL_NAME, "GoodGame");
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            audit(e);
        }
    }

    void onChannelGoOffline(ChannelContainer channelContainer,Stream currentStream){
        Duration streamDuration;
        try {
            streamDuration = Duration.between(currentStream.getStreamStartTime(),
                    LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        } catch (Exception e) {
            streamDuration = Duration.ZERO;
            audit(e);
        }
        try {
            String message = "⚫️ Стрим <a href=\"https://goodgame.ru/channel/"+GG_CHANNEL_NAME+"\">"+GG_CHANNEL_NAME+"</a> на GoodGame окончен ⚫️ \n" +
                    "Длительность: " + streamDuration.toHours() + " ч. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " мин.\n" +
                    "Зрителей: " + channelContainer.getViewers();

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message,true);

            streamService.setStreamOfflineById(Instant.now(), currentStream.getId());
        } catch (Exception e) {
            audit(e);
        } finally {
            currentStream = null;
        }
    }

    private static StreamStatus strToStreamStatus(String str) {
        switch (str) {
            case "Live":
                return StreamStatus.LIVE;
            default:
                return StreamStatus.OFFLINE;
        }
    }

    public void audit(Exception exception) {
        tgBot.sendTextMessageToChannel(AUDIT_TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(exception));
        logger.error(ExceptionUtils.getFullStackTrace(exception));
    }
}
