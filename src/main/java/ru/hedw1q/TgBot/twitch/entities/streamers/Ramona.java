package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author hedw1q
 */
public class Ramona extends FemaleTwitchStreamer {

    private LocalTime subExecutionTime;
    private static int subExecutionDelayInSeconds = 2;

    public Ramona(String channelName, AuthData authData) {
        super(channelName, authData);
        registerNewEvent(ChannelChangeTitleEvent.class, this::onChannelChangeTitleEvent);
        subExecutionTime = LocalTime.now();
    }

    public void onChannelChangeTitleEvent(ChannelChangeTitleEvent event) {
        String message = "❗️" + channelName + " сменила название стрима ❗️\n" +
                "Категория: " + event.getStream().getGameName() + "\n" +
                "Название: " + event.getStream().getTitle();

        String thumbnailUrl = event.getStream().getThumbnailUrl(1600, 900);

        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);
            audit(tgBot, e);
        }
    }

    @Override
    public void onChannelSubscriptionEvent(SubscriptionEvent event) {
        if (Duration.between(subExecutionTime, LocalTime.now()).toMillis() < subExecutionDelayInSeconds*1000 || event.getUser().getName().equals("hedw1q")) {
            audit(tgBot, "Skipped sub by " + event.getUser().getName());
            return;
        }

        try {
            audit(tgBot, "Sub by "+event.getUser().getName());
            //ot 4 do 8
            TimeUnit.SECONDS.sleep(new Random().nextInt(6) + 3);

            twitchChat.sendMessage(event.getChannel().getName(), "honeyr1WOW honeyr1WOW honeyr1WOW ");
        } catch (InterruptedException ie) {
        } finally {
            subExecutionTime = LocalTime.now();
        }
    }

    @Override
    public void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        subExecutionTime = LocalTime.now();
        Stream newStream = new Stream(channelGoLiveEvent.getChannel().getName(), LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC), "Twitch");

        String message = "\uD83D\uDFE3 Рамона завела, хихихи \uD83D\uDFE3 \n" +
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

}
