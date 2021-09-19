package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.services.StreamService;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author hedw1q
 */

public abstract class BaseStreamer {
    @Getter
    @Setter
    private String channelName;

    @Autowired
    protected TgBot tgBot;
    @Autowired
    protected StreamService streamService;

    protected Stream currentStream;

    public BaseStreamer(String channelName) {
        this.channelName = channelName;
    }

    protected void onChannelSubscriptionEvent(SubscriptionEvent event) {
    }

    protected void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        currentStream=new Stream(channelName, LocalDateTime.channelGoLiveEvent.getStream().getStartedAtInstant() )
                .setStreamStartTime(channelGoLiveEvent.getStream().getStartedAtInstant());
        try {
            String message = "❗️Поток от " + channelGoLiveEvent.getChannel().getName() + " на Twitch ❗️\n" +
                    "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                    "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

            String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(streamStartTime, channelGoLiveEvent.getChannel().getName());
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(e));
            logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            streamStartTime = null;
            streamFinishTime = null;
            channelViewerCount = 0;
        }
    }
}

