package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import ru.hedw1q.TgBot.twitch.config.AuthData;

import java.util.concurrent.TimeUnit;

/**
 * @author hedw1q
 */
public class Ramona extends FemaleStreamer {


    public Ramona(String channelName, AuthData authData) {
        super(channelName, authData);
    }

    @Override
    protected void onChannelSubscriptionEvent(SubscriptionEvent event) {
        tgBot.sendTextMessageToChannel(890471143L, event.toString());

        try{
            TimeUnit.SECONDS.sleep(1);
            twitchChat.sendMessage(event.getChannel().getName(), "honeyr1WOW honeyr1WOW honeyr1WOW ");
        }catch (InterruptedException ie){ }
    }

}
