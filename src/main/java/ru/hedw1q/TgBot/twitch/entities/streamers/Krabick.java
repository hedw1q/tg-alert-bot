package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author hedw1q
 */
public class Krabick extends MaleTwitchStreamer {

    public Krabick(String channelName, AuthData authData) {
        super(channelName, authData);
    }

    @Override
    public void onChannelSubscriptionEvent(SubscriptionEvent event) {
//        try{
//            TimeUnit.MILLISECONDS.sleep(1500);
//            twitchChat.sendMessage(event.getChannel().getName(),getRandomSubMessage());
//        }catch (InterruptedException ie){ }
    }

    @Override
    public void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) {
        Stream newStream = new Stream(channelGoLiveEvent.getChannel().getName(), LocalDateTime.ofInstant(channelGoLiveEvent.getStream().getStartedAtInstant(), ZoneOffset.UTC), "Twitch");

        String message = "\uD83E\uDD80 Крабик завел на твиче \uD83E\uDD80️\n" +
                "Название: " + channelGoLiveEvent.getStream().getTitle() + "\n" +
                "Категория: " + channelGoLiveEvent.getStream().getGameName() + "\n" +
                "\n" +
                "Ссылка: https://www.twitch.tv/" + channelGoLiveEvent.getChannel().getName();

        String thumbnailUrl = channelGoLiveEvent.getStream().getThumbnailUrl(1600, 900);

        try {
            tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, thumbnailUrl, message);

            streamService.createNewStream(newStream.getStreamStartTime().toInstant(ZoneOffset.UTC), channelGoLiveEvent.getChannel().getName(), "Twitch");
        } catch (Exception e) {
            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message, true);
            audit(e);
        } finally {
            channelViewerCount = 0;
        }
    }

    @Override
    public void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) {
        Duration streamDuration;
        Integer streamId = null;
        Stream finishedStream = streamService.getLastStreamByChannelName(channelGoOfflineEvent.getChannel().getName());
        try {
            streamId = finishedStream.getId();
            streamDuration = Duration.between(finishedStream.getStreamStartTime(),
                    LocalDateTime.ofInstant(channelGoOfflineEvent.getFiredAtInstant(),ZoneOffset.UTC));
        } catch (Exception e) {
            streamDuration = Duration.ZERO;
            audit(e);
        }
        try {
            String message = "⚫️Стрим <a href=\"https://twitch.tv/Krabick\">Krabick</a> на Twitch окончен ⚫️\n" +
                    "Длительность: " + streamDuration.toHours() + " ч. " + (streamDuration.toMinutes() - streamDuration.toHours() * 60) + " мин.\n" +
                    "Зрителей: " + channelViewerCount;

            tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, message);

            streamService.setStreamOfflineById(channelGoOfflineEvent.getFiredAtInstant(), streamId);
        } catch (Exception e) {
            audit(e);
        } finally {
            channelViewerCount = 0;
        }
    }


    private static String getRandomSubMessage(){
        List<String> messageList=new ArrayList<>(Arrays.asList(
                "krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy krabLove krabAF krabZloopy",
                "krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF krabFUN2 krabFUN2 krabBlink krabAF",
                "krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13 krab13 krabBlink krab13"));
        Random rand=new Random();

        return messageList.get(rand.nextInt(messageList.size()));
    }
}
