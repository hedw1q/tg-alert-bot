package ru.hedw1q.TgBot.twitch.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hedw1q.TgBot.twitch.entities.Streamer;
import ru.hedw1q.TgBot.twitch.repositories.StreamRepository;
import ru.hedw1q.TgBot.twitch.repositories.StreamerRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author hedw1q
 */
@Service
public class StreamerServiceImpl implements StreamerService {
    @PersistenceContext
    EntityManager em;
    @Autowired
    private StreamerRepository streamerRepository;

    @Override
    public List<Streamer> getStreamers() {
        List<Streamer> streamers = streamerRepository.findAll();
        if (streamers == null || streamers.isEmpty()) return Collections.emptyList();
        return streamers;
    }

    @Override
    public List<Streamer> getStreamersByPlatform(String platform) {
        List<Streamer> streamers = streamerRepository.findByPlatform(platform);
        if (streamers == null) return new ArrayList<Streamer>();
        return streamers;
    }

    @Override
    public Streamer getStreamerByChannelNameAndPlatform(String channelName, String platform) {
        List<Streamer> streamers = streamerRepository.findByChannelNameAndPlatform(channelName, platform);
        if (streamers.size() != 0) return streamers.get(0);
        return null;
    }

    @Override
    public Streamer addNewStreamerIfNotExist(Streamer streamer) {
        List<Streamer> streamers = streamerRepository.findByChannelNameAndPlatform(streamer.getChannelName(), streamer.getPlatform());
        if(streamers==null){
            return streamerRepository.save(streamer);
        }
        return streamers.get(0);
    }
}
