package ru.hedw1q.TgBot.twitch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.LocalDateTime;

/**
 * @author hedw1q
 */
public interface StreamRepository extends JpaRepository<Stream, Integer> {

    @Query(value="SELECT * " +
            "FROM public.streams " +
            "WHERE channel_name =?1 and " +
            "stream_status='LIVE' and " +
            "id=(SELECT max(id) FROM streams where channel_name=?1)", nativeQuery = true)
    Stream findCurrentStreamByChannelName(String channelName);

    @Query(value="UPDATE public.streams " +
            "SET stream_finish_time=?1, stream_status='OFFLINE' " +
            "WHERE id=?2", nativeQuery = true)
    void updateStreamSetOfflineById(LocalDateTime streamFinishTime, Integer streamId);
}
