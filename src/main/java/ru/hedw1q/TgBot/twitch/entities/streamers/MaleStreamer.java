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
public class MaleStreamer extends BaseStreamer {

    public MaleStreamer(String channelName, AuthData authData) {
        super(channelName, authData);
    }

    @Override
    public void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, channelGoLiveEvent.toString());

        Stream newStream = new Stream(channelName, LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC));

        String message = "❗️" + channelName + " завел на Twitch ❗️\n" +
                "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                "\n" +
                "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();
        String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);
        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(newStream.getStreamStartTime().toInstant(ZoneOffset.UTC), channelName);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            logger.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            channelViewerCount = 0;
        }
    }
//krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13
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
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }

}
