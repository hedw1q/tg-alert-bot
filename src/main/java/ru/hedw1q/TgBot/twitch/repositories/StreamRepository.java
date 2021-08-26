package ru.hedw1q.TgBot.twitch.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hedw1q.TgBot.twitch.entities.Stream;

import java.time.LocalDateTime;

/**
 * @author hedw1q
 */
public interface StreamRepository extends JpaRepository<Stream, Integer> {

    @Query("SELECT u \n" +
            "FROM streams u \n" +
            "WHERE channel_name =?1' and \n" +
            "stream_status='LIVE' and \n" +
            "id=(SELECT max(id) FROM streams where channel_name=?1')")
    Stream findCurrentStreamByChannelName(String channelName);

    @Query("UPDATE streams u \n" +
            "SET stream_finish_time=?1, stream_status='OFFLINE'\n" +
            "WHERE id=?2;")
    void updateStreamSetOfflineById(LocalDateTime streamFinishTime, Integer streamId);
}
