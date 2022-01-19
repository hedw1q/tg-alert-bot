package ru.hedw1q.TgBot.twitch.entities.streamers;


import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.Streamer;
import ru.hedw1q.TgBot.twitch.services.StreamerService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
public class MaleTwitchStreamer extends BaseTwitchStreamer {

    public MaleTwitchStreamer(String channelName, AuthData authData) {
        super(channelName, authData);
    }

    @Override
    @PostConstruct
    protected void afterInit() {
        streamerService.addNewStreamerIfNotExist(new Streamer(channelName, "Twitch", 'M'));
    }

    @Override
    public void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        Stream newStream = new Stream(channelName, LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC), "Twitch");

        String message = "❗️" + channelName + " завел на Twitch ❗️\n" +
                "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                "\n" +
                "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();
        String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);
        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(newStream.getStreamStartTime().toInstant(ZoneOffset.UTC), channelName, "Twitch");
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            audit(tgBot, e);
        } finally {
            channelViewerCount = 0;
        }
    }

    @Override
    public void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent) {
        try {
            String message = "❗️" + channelName + " сменил игру на стриме ❗️\n" +
                    "Категория: " + channelChangeGameEvent.getStream().getGameName() + "\n" +
                    "Название: " + channelChangeGameEvent.getStream().getTitle();

            String thumbnailUrl = channelChangeGameEvent.getStream().getThumbnailUrl(1600, 900);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(e));
            audit(tgBot, e);
        }
    }

    @Override
    public void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        super.onChannelGoOffline(channelGoOfflineEvent);
    }
}
