package ru.hedw1q.TgBot.twitch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.Streamer;

import java.util.List;

/**
 * @author hedw1q
 */
public interface StreamerRepository extends JpaRepository<Streamer, Integer> {

    List<Streamer> findByChannelNameAndPlatform(String channelName,String platform);

    List<Streamer> findByPlatform(String platform);
}
