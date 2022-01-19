package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import ru.hedw1q.TgBot.twitch.config.AuthData;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.Streamer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author hedw1q
 */
public class Zanuda extends FemaleTwitchStreamer{

    private LocalTime subExecutionTime;
    private static int subExecutionDelayInSeconds = 2;

    private boolean subEnabled;
    private List<String> msgList;

    public Zanuda(String channelName, AuthData authData) {
        super(channelName, authData);
        subExecutionTime = LocalTime.now();
        msgList= Arrays.asList("etz3 etz3 etz3 ", "etz33 etz33 etz33 ","etz333 etz333 etz333 ");
    }

    @Override
    public void onChannelSubscriptionEvent(SubscriptionEvent event) {
        if (!checkEnabled()) return;

        if (Duration.between(subExecutionTime, LocalTime.now()).toMillis() < subExecutionDelayInSeconds*1000 || event.getUser().getName().equals("hedw1q")) {
            audit(tgBot, "Skipped sub by " + event.getUser().getName());
            subExecutionTime = LocalTime.now();
            return;
        }

        try {
            audit(tgBot, "Sub by "+event.getUser().getName());
            //ot 4 do 8
            TimeUnit.SECONDS.sleep(new Random().nextInt(7) + 3);

            twitchChat.sendMessage(event.getChannel().getName(), msgList.get(new Random().nextInt(msgList.size())));
        } catch (InterruptedException ie) {
        } finally {
            subExecutionTime = LocalTime.now();
        }
    }

    @Override
    public void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent) { }

    @Override
    public void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent) { }

    @Override
    public void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent) { }

    private boolean checkEnabled(){
        return isSubEnabled();
    }

    public boolean isSubEnabled() {
        return streamerService.getStreamerByChannelNameAndPlatform(channelName, "Twitch").isSubEvent();
    }

    public void setSubEnabled(boolean subEnabled) {
        Streamer streamer=new Streamer(channelName,"Twitch");
        streamer.setSubEvent(subEnabled);
        streamerService.updateStreamerSetSubEvent(streamer);
    }
}
