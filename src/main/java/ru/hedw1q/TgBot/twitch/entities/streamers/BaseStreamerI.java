package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import org.apache.commons.lang.exception.ExceptionUtils;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
public interface BaseStreamerI {
    void onChannelGoLive(ChannelGoLiveEvent channelGoLiveEvent);

    void onChannelSubscriptionEvent(SubscriptionEvent event);

    void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent channelViewerCountUpdateEvent);

    void onChannelGoOffline(ChannelGoOfflineEvent channelGoOfflineEvent);

    void onChannelChangeGame(ChannelChangeGameEvent channelChangeGameEvent);

}
