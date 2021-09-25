package ru.hedw1q.TgBot.twitch.db;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.StreamStatus;
import ru.hedw1q.TgBot.twitch.services.StreamService;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hedw1q
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StreamServiceTest {

    @Autowired
    private StreamService streamService;

    private static int TEST_STREAM_ID;
    private static String TEST_CHANNEL_NAME = "testChannelName";
    private static Instant TEST_TIME=Instant.ofEpochSecond(1261440000);

    @Test
    @Order(1)
    void testCreateNewStream() {
        Stream newStream = streamService.createNewStream(TEST_TIME, TEST_CHANNEL_NAME);

        TEST_STREAM_ID = newStream.getId();

        assertThat(TEST_STREAM_ID).isNotNull();
        assertThat(newStream.getStreamFinishTime()).isNull();
        assertThat(newStream.getStreamStatus()).isEqualTo(StreamStatus.LIVE);
        assertThat(newStream.getStreamStartTime()).isEqualTo(LocalDateTime.ofInstant(TEST_TIME, ZoneOffset.UTC));
        assertThat(newStream.getChannelName()).isEqualTo(TEST_CHANNEL_NAME);
    }

    @Test
    @Order(2)
    void testGetStreamById(){
        Stream stream=streamService.getStreamById(TEST_STREAM_ID);

        assertThat(stream.getChannelName()).isEqualTo(TEST_CHANNEL_NAME);
    }

    @Test
    @Order(3)
    void testGetLastStreamByChannelName(){
        Stream stream=streamService.getLastStreamByChannelName(TEST_CHANNEL_NAME);

        assertThat(stream.getId()).isEqualTo(TEST_STREAM_ID);
    }

    @Test
    @Order(4)
    void testSetStreamOfflineById(){
        streamService.setStreamOfflineById(TEST_TIME,TEST_STREAM_ID);

        Stream updatedStream=streamService.getStreamById(TEST_STREAM_ID);

        assertThat(updatedStream.getStreamFinishTime()).isEqualTo(LocalDateTime.ofInstant(TEST_TIME, ZoneOffset.UTC));
        assertThat(updatedStream.getStreamStatus()).isEqualTo(StreamStatus.OFFLINE);
    }

    @Test
    @Order(5)
    void testDeleteStreamById(){
        assertThat(streamService.deleteStreamById(TEST_STREAM_ID)).isTrue();
    }
}
