package ru.hedw1q.TgBot.twitch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.Streamer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hedw1q
 */
public interface StreamerRepository extends JpaRepository<Streamer, Integer> {

    List<Streamer> findByChannelNameAndPlatform(String channelName,String platform);

    List<Streamer> findByPlatform(String platform);

    @Query("update Streamer u set u.subEvent = :param where u.channelName = :name and u.platform=:platform")
    @Modifying
    @Transactional
    void updateStreamerSetSubEvent(@Param("param") boolean subEventStatus, @Param("platform") String platform, @Param("name") String channelName);
}
