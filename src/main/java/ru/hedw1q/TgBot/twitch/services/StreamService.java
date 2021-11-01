package ru.hedw1q.TgBot.twitch.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.Instant;

/**
 * @author hedw1q
 */
public interface StreamService {

    Logger logger = LoggerFactory.getLogger(StreamService.class);

    Stream createNewStream(Instant startTime, String channelName, String platform);

    Stream getCurrentLiveStreamByChannelName(String channelName);

    void setStreamOfflineById(Instant finishTime,Integer streamId);

    Stream getStreamById(Integer streamId);

    boolean deleteStreamById(Integer streamId);
}
