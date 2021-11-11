package ru.hedw1q.TgBot.twitch.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.Streamer;

import java.util.List;

/**
 * @author hedw1q
 */
public interface StreamerService {

    Logger logger = LoggerFactory.getLogger(StreamService.class);

    List<Streamer> getStreamers();

    List<Streamer> getStreamersByPlatform(String platform);

    Streamer getStreamerByChannelNameAndPlatform(String channelName,String platform);

    Streamer addNewStreamerIfNotExist(Streamer streamer);

    void updateStreamerSetSubEvent(Streamer streamer);
}
