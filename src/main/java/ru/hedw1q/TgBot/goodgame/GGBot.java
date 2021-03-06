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
import ru.hedw1q.TgBot.twitch.entities.Streamer;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.hedw1q.TgBot.twitch.services.StreamerService;
import ru.maximkulikov.goodgame.api.GoodGame;
import ru.maximkulikov.goodgame.api.handlers.StreamChannelResponseHandler;
import ru.maximkulikov.goodgame.api.models.ChannelContainer;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static ru.hedw1q.TgBot.twitch.entities.streamers.BaseTwitchStreamer.audit;

/**
 * @author hedw1q
 */
@Component
@EnableAsync
public class GGBot {
    public static Long TG_CHANNEL_ID = -1001537091172L;
    public static String GG_CHANNEL_NAME = "KinoKrabick";
    private final Logger logger = LoggerFactory.getLogger(GGBot.class);

    @Autowired
    StreamService streamService;
    @Autowired
    StreamerService streamerService;
    @Autowired
    TgBot tgBot;
    GoodGame gg = new GoodGame();

    StreamStatus streamStatus = StreamStatus.OFFLINE;

    @PostConstruct
    void afterInit(){
        streamerService.addNewStreamerIfNotExist(new Streamer(GG_CHANNEL_NAME,"GoodGame",'M'));
    }

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
                Stream currentStream = streamService.getCurrentLiveStreamByChannelName(GG_CHANNEL_NAME);

                if (streamStatus.equals(StreamStatus.LIVE) && currentStream == null) {
                    onChannelGoLive(channelContainer);
                } else if (streamStatus.equals(StreamStatus.OFFLINE) && currentStream != null) {
                    onChannelGoOffline(channelContainer, currentStream);
                }
                streamStatus = null;
                currentStream = null;
            }
        });
    }

    String onChannelGoLive(ChannelContainer channelContainer) {
        String message = "??????"+GG_CHANNEL_NAME+" ???????????? ???? GoodGame ??????\n" +
                "????????????????: " + channelContainer.getChannel().getTitle() + "\n" +
                "??????????????????: " + channelContainer.getChannel().getGames().get(0).getTitle() + "\n" +
                "\n" +
                "????????????: https://goodgame.ru/channel/" + GG_CHANNEL_NAME;
        try {
            String thumbnailUrl = "https:" + channelContainer.getChannel().getThumb();
           // audit(tgBot,thumbnailUrl);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
            //tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message, true);

            streamService.createNewStream(Instant.now(), GG_CHANNEL_NAME, "GoodGame");
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message, true);
            audit(tgBot, logger, e);
        }
        return message;
    }

    String onChannelGoOffline(ChannelContainer channelContainer, Stream currentStream) {
        Duration streamDuration;
        String message=null;
        try {
            streamDuration = Duration.between(currentStream.getStreamStartTime(),
                    LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        } catch (Exception e) {
            streamDuration = Duration.ZERO;
            audit(tgBot, logger, e);
        }
        try {
            message = "?????????? <a href=\"https://goodgame.ru/channel/" + GG_CHANNEL_NAME + "\">" + GG_CHANNEL_NAME + "</a> ???? GoodGame ??????????????\n" +
                    "????????????????????????: " + streamDuration.toHours() + " ??. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " ??????.\n" +
                    "????????????????: " + channelContainer.getViewers();

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message, true);

            streamService.setStreamOfflineById(Instant.now(), currentStream.getId());
        } catch (Exception e) {
            audit(tgBot, logger, e);
        } finally {
            currentStream = null;
        }
        return message;
    }

    private static StreamStatus strToStreamStatus(String str) {
        switch (str) {
            case "Live":
                return StreamStatus.LIVE;
            default:
                return StreamStatus.OFFLINE;
        }
    }

}
