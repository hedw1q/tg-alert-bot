package ru.hedw1q.TgBot.twitch.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hedw1q.TgBot.twitch.TwBot;
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
    private static final Logger logger = LoggerFactory.getLogger(StreamService.class);


    public void createNewStream(Instant startTime, String channelName){
        Stream stream=new Stream(channelName, LocalDateTime.ofInstant(startTime, ZoneOffset.UTC));
        streamRepository.save(stream);
    }

    public Stream getLastStreamByChannelName(String channelName){
        return streamRepository.findCurrentStreamByChannelName(channelName);
    }

    public void setStreamOfflineById(Instant finishTime,Integer streamId){
        streamRepository.updateStreamSetOfflineById(LocalDateTime.ofInstant(finishTime, ZoneOffset.UTC),streamId);
    }

    public Stream getStreamById(Integer streamId){
        return streamRepository.findById(streamId).orElseThrow();
    }

    public void deleteStreamById(Integer streamId){
        streamRepository.deleteById(streamId);
    }
}
