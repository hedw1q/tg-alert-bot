package ru.hedw1q.TgBot.twitch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * @author hedw1q
 */
public interface StreamRepository extends JpaRepository<Stream, Integer> {

    @Query(value="SELECT * " +
            "FROM public.streams " +
            "WHERE channel_name ILIKE :name and " +
            "stream_status='LIVE' and " +
            "id=(SELECT max(id) FROM streams where channel_name ILIKE :name)", nativeQuery = true)
    Stream findCurrentStreamByChannelName(@Param("name") String channelName);

    @Query(value="UPDATE streams " +
            "SET stream_finish_time=:time, stream_status='OFFLINE' " +
            "WHERE id=:id", nativeQuery = true)
    @Modifying
    @Transactional
    void updateStreamSetOfflineById(@Param("time") LocalDateTime streamFinishTime, @Param("id") Integer streamId);
}
