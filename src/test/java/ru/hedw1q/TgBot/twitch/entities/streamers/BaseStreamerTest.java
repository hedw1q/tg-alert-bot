package ru.hedw1q.TgBot.twitch.entities.streamers;

import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import com.github.twitch4j.helix.domain.Stream;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ru.hedw1q.TgBot.twitch.TwBot;
import ru.hedw1q.TgBot.twitch.entities.StreamStatus;
import ru.hedw1q.TgBot.twitch.entities.streamers.BaseStreamer;
import ru.hedw1q.TgBot.twitch.services.StreamService;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;


import static ru.hedw1q.TgBot.TgBotApplicationTests.TEST_TELEGRAM_CHANNEL_ID;

/**
 * @author hedw1q
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseStreamerTest {

    BaseStreamer testStreamer=new BaseStreamer() {};

    @Autowired
    private StreamService streamService;
    @Mock
    private ChannelChangeGameEvent spyChannelChangeGameEvent;
    @Mock
    private ChannelGoLiveEvent spyChannelGoLiveEvent;
    @Mock
    private ChannelGoOfflineEvent spyChannelGoOfflineEvent;
    @Mock
    private ChannelViewerCountUpdateEvent spyChannelViewerCountUpdateEvent;

    private static int TEST_STREAM_ID;



    @BeforeEach
     void setUp() {
        TwBot.TG_CHANNEL_ID = TEST_TELEGRAM_CHANNEL_ID;

        Stream streamSpy = Mockito.mock(Stream.class);
        EventChannel channelSpy = Mockito.mock(EventChannel.class);

        Mockito.when(spyChannelChangeGameEvent.getStream()).thenReturn(streamSpy);
        Mockito.when(spyChannelGoLiveEvent.getStream()).thenReturn(streamSpy);
        Mockito.when(spyChannelChangeGameEvent.getChannel()).thenReturn(channelSpy);
        Mockito.when(spyChannelGoOfflineEvent.getChannel()).thenReturn(channelSpy);
        Mockito.when(spyChannelGoLiveEvent.getChannel()).thenReturn(channelSpy);
        Mockito.when(spyChannelViewerCountUpdateEvent.getViewerCount()).thenReturn(500);


        Mockito.when(spyChannelGoOfflineEvent.getFiredAtInstant()).thenReturn(Instant.ofEpochSecond(1261443700));
        Mockito.when(streamSpy.getStartedAtInstant()).thenReturn(Instant.ofEpochSecond(1261440000));
        Mockito.when(streamSpy.getViewerCount()).thenReturn(228);
        Mockito.when(streamSpy.getTitle()).thenReturn("Title");
        Mockito.when(streamSpy.getGameName()).thenReturn("GameName");
        Mockito.when(channelSpy.getName()).thenReturn("ChannelName");
        Mockito.when(streamSpy.getThumbnailUrl(1600,900)).thenReturn("https://cdn.pixabay.com/photo/2013/07/12/17/47/test-pattern-152459_960_720.png");
    }


    @Test
    @Order(1)
    void channelGoLiveTest() {
        testStreamer.onChannelGoLive(this.spyChannelGoLiveEvent);
        try {
            ru.hedw1q.TgBot.twitch.entities.Stream stream = streamService.getLastStreamByChannelName("ChannelName");
            TEST_STREAM_ID=stream.getId();

            assertThat(stream.getStreamStatus()).isEqualTo(StreamStatus.LIVE);
            assertThat(stream.getChannelName()).isEqualTo("ChannelName");
            assertThat(stream.getStreamStartTime()).isEqualTo( LocalDateTime.ofInstant(Instant.ofEpochSecond(1261440000), ZoneOffset.UTC));
            assertThat(stream.getStreamFinishTime()).isNull();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void channelChangeGameTest(){
        testStreamer.onChannelChangeGame(this.spyChannelChangeGameEvent);
    }

    @Test
    @Order(3)
    void channelGoOfflineTest() {
        testStreamer.onChannelViewerCountUpdate(this.spyChannelViewerCountUpdateEvent);

        try {
            testStreamer.onChannelGoOffline(this.spyChannelGoOfflineEvent);

            ru.hedw1q.TgBot.twitch.entities.Stream stream=streamService.getStreamById(TEST_STREAM_ID);

            assertThat(stream.getStreamStatus()).isEqualTo(StreamStatus.OFFLINE);
            assertThat(stream.getStreamFinishTime()).isNotNull();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @AfterAll
    void tearDown(){
        streamService.deleteStreamById(TEST_STREAM_ID);
    }
}
