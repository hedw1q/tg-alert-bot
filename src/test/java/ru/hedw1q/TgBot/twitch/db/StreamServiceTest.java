package ru.hedw1q.TgBot.twitch.db;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.StreamStatus;
import ru.hedw1q.TgBot.twitch.repositories.StreamRepository;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.hedw1q.TgBot.twitch.services.StreamServiceImpl;

import java.time.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


/**
 * @author hedw1q
 */
@ExtendWith(MockitoExtension.class)
public class StreamServiceTest {

    @InjectMocks
    private StreamServiceImpl streamService;
    @Mock
    private StreamRepository streamRepository;

    private static int TEST_STREAM_ID = 1;
    private static String TEST_CHANNEL_NAME = "testChannelName";
    private static Instant TEST_INSTANT_TIME = Instant.ofEpochSecond(1261440000);
    private static LocalDateTime TEST_LDT_TIME=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    private static String TEST_PLATFORM = "Twitch";


    @Test
    void testCreateNewStream() {
        when(streamRepository.save(Mockito.any(Stream.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Stream stream=streamService.createNewStream(TEST_INSTANT_TIME, TEST_CHANNEL_NAME, TEST_PLATFORM);

        assertThat(stream.getStreamStatus()).isEqualTo(StreamStatus.LIVE);
        assertThat(stream.getStreamStartTime()).isNotNull();
        assertThat(stream.getStreamFinishTime()).isNull();
    }

    @Test()
    void testGetStreamById_WhenNotFound() {
        when(streamRepository.findById(TEST_STREAM_ID)).thenReturn(null);

        assertThrows(NullPointerException.class,()->{
            streamService.getStreamById(TEST_STREAM_ID);
        });
    }

    @Test
    void testGetLastStreamByChannelName() {
        Stream stream=new Stream();
        stream.setChannelName(TEST_CHANNEL_NAME);
        stream.setStreamStartTime(LocalDateTime.now());
        stream.setPlatform(TEST_PLATFORM);
        stream.setStreamStatus(StreamStatus.LIVE);
        stream.setId(TEST_STREAM_ID);
        when(streamRepository.findCurrentStreamByChannelName(TEST_CHANNEL_NAME)).thenReturn(stream);

        Stream getStream = streamService.getLastStreamByChannelName(TEST_CHANNEL_NAME);

        assertThat(stream).isEqualTo(getStream);
    }

    @Test
    void testSetStreamOfflineById() {
        Stream stream=new Stream();
        stream.setChannelName(TEST_CHANNEL_NAME);
        stream.setPlatform(TEST_PLATFORM);
        stream.setStreamStatus(StreamStatus.OFFLINE);
        stream.setStreamFinishTime(TEST_LDT_TIME);
        stream.setId(TEST_STREAM_ID);

        streamService.setStreamOfflineById(TEST_INSTANT_TIME,TEST_STREAM_ID);

        verify(streamRepository,times(1)).updateStreamSetOfflineById(any(),anyInt());
    }

    @Test
    void testDeleteStreamById_WhenException() {
        doThrow(EmptyResultDataAccessException.class).when(streamRepository).deleteById(TEST_STREAM_ID);

        assertThat(streamService.deleteStreamById(TEST_STREAM_ID)).isFalse();
    }

    @Test
    void testDeleteStreamById() {
        assertThat(streamService.deleteStreamById(TEST_STREAM_ID)).isTrue();
    }
}
