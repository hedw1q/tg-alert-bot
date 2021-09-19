package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import org.apache.commons.lang.exception.ExceptionUtils;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
public class FemaleStreamer extends BaseStreamer{

    public FemaleStreamer(String channelName, AuthData authData) {
        super(channelName, authData);
    }

    @Override
    protected void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {

        Stream newStream = new Stream(channelName, LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC));

        try {
            String message = "❗️" + channelGoLiveEvent.getChannel().getName() + "завела на Twitch ❗️\n" +
                    "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                    "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                    "\n" +
                    "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

            String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(newStream.getStreamStartTime().toInstant(ZoneOffset.UTC), channelName);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(e));
            logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            newStream = null;
            channelViewerCount = 0;
        }
    }

    @Override
    protected void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent) {
        try {
            String message = "❗️" + channelChangeGameEvent.getChannel().getName() + " сменила игру на стриме ❗️\n" +
                    "Категория: " + channelChangeGameEvent.getStream().getGameName() + "\n" +
                    "Название: " + channelChangeGameEvent.getStream().getTitle();

            String thumbnailUrl = channelChangeGameEvent.getStream().getThumbnailUrl(1600, 900);

            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, ExceptionUtils.getFullStackTrace(e));
        }
    }
}
