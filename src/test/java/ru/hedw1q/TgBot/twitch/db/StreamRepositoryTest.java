package ru.hedw1q.TgBot.twitch.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.StreamStatus;
import ru.hedw1q.TgBot.twitch.repositories.StreamRepository;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hedw1q
 */
@DataJpaTest
public class StreamRepositoryTest {

    @Autowired
    private StreamRepository streamRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void testFindCurrentStreamByChannelName_WhenEmpty() {
        Stream stream = streamRepository.findCurrentStreamByChannelName("Empty");

        assertThat(stream).isNull();
    }

    @Test
    void testFindCurrentStreamByChannelName() {
        Stream stream = new Stream("Channel", LocalDateTime.now(),"Twitch");

        entityManager.merge(stream);

        Stream extractedStream=streamRepository.findCurrentStreamByChannelName(stream.getChannelName());
        System.out.println(extractedStream.toString());
        assertThat(stream).isEqualTo(extractedStream);
    }

    @Test
    void testUpdateStreamSetOfflineById(){
        Stream stream = new Stream("Channel", LocalDateTime.of(LocalDate.now(), LocalTime.MIN),"Twitch");

        entityManager.persist(stream);

        streamRepository.updateStreamSetOfflineById(LocalDateTime.now(),stream.getId());
        entityManager.refresh(stream);
        System.out.println(stream.toString());
        assertThat(stream.getStreamStatus()).isEqualTo(StreamStatus.OFFLINE);
        assertThat(stream.getStreamFinishTime()).isNotNull();
    }


}
