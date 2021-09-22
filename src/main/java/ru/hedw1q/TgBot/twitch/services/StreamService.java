package ru.hedw1q.TgBot.twitch.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
public interface StreamService {

    Logger logger = LoggerFactory.getLogger(StreamService.class);

    void createNewStream(Instant startTime, String channelName);

    Stream getLastStreamByChannelName(String channelName);

    void setStreamOfflineById(Instant finishTime,Integer streamId);

    Stream getStreamById(Integer streamId);

    void deleteStreamById(Integer streamId);
}
