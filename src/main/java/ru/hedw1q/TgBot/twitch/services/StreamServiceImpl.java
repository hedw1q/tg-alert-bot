package ru.hedw1q.TgBot.twitch.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.repositories.StreamRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.StreamCorruptedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
@Service
public class StreamServiceImpl implements StreamService {
    @PersistenceContext
    EntityManager em;
    @Autowired
    private StreamRepository streamRepository;

    @Override
    public Stream createNewStream(Instant startTime, String channelName) {
        Stream stream = new Stream(channelName, LocalDateTime.ofInstant(startTime, ZoneOffset.UTC));
        try {
            return streamRepository.save(stream);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Stream getLastStreamByChannelName(String channelName) {
        return streamRepository.findCurrentStreamByChannelName(channelName);
    }

    @Override
    public void setStreamOfflineById(Instant finishTime, Integer streamId) {
        streamRepository.updateStreamSetOfflineById(LocalDateTime.ofInstant(finishTime, ZoneOffset.UTC), streamId);
    }

    @Override
    public Stream getStreamById(Integer streamId) {
        return streamRepository.findById(streamId).orElseThrow(() -> {
            throw new RuntimeException();
        });
    }

    @Override
    public boolean deleteStreamById(Integer streamId) {
        try{
            streamRepository.deleteById(streamId);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
