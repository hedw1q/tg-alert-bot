package ru.hedw1q.TgBot.twitch.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.repositories.StreamRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
@Service
public class StreamService {
    @PersistenceContext
    EntityManager em;
    @Autowired
    private StreamRepository streamRepository;

    public void createNewStream(Instant startTime, String channelName){
        streamRepository.save(new Stream(channelName, LocalDateTime.ofInstant(startTime, ZoneOffset.UTC)));
    }

    public Stream getLastStreamByChannelName(String channelName) throws SQLException {
        return streamRepository.findCurrentStreamByChannelName(channelName);
    }

    public void setStreamOfflineById(Instant finishTime,Integer streamId) throws SQLException{
        streamRepository.updateStreamSetOfflineById(LocalDateTime.ofInstant(finishTime, ZoneOffset.UTC),streamId);
    }
}
