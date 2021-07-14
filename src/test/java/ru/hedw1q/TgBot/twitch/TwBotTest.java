package ru.hedw1q.TgBot.twitch;

import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import com.github.twitch4j.helix.domain.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static ru.hedw1q.TgBot.TgBotApplicationTests.TEST_TELEGRAM_CHANNEL_ID;

/**
 * @author hedw1q
 */
@SpringBootTest
public class TwBotTest {
    @Autowired
    TwBot twBot;
    @Mock
    private ChannelGoLiveEvent spyChannelGoLiveEvent;
    @Mock
    private ChannelGoOfflineEvent spyChannelGoOfflineEvent;
    @Mock
    private ChannelViewerCountUpdateEvent spyChannelViewerCountUpdateEvent;

    @BeforeEach
     void setUp() {
        TwBot.TG_CHANNEL_ID = TEST_TELEGRAM_CHANNEL_ID;

        Stream streamSpy = Mockito.mock(Stream.class);
        EventChannel channelSpy = Mockito.mock(EventChannel.class);

        Mockito.when(spyChannelGoLiveEvent.getStream()).thenReturn(streamSpy);
        Mockito.when(spyChannelGoOfflineEvent.getChannel()).thenReturn(channelSpy);
        Mockito.when(spyChannelGoLiveEvent.getChannel()).thenReturn(channelSpy);
        Mockito.when(spyChannelViewerCountUpdateEvent.getViewerCount()).thenReturn(500);

        Mockito.when(streamSpy.getStartedAtInstant()).thenReturn(Instant.ofEpochSecond(0));
        Mockito.when(streamSpy.getTitle()).thenReturn("Title");
        Mockito.when(streamSpy.getGameName()).thenReturn("GameName");
        Mockito.when(channelSpy.getName()).thenReturn("ChannelName");
        Mockito.when(streamSpy.getThumbnailUrl()).thenReturn("https://static-cdn.jtvnw.net/previews-ttv/live_user_timofey-320x180.jpg");
    }


    @Test
    void channelGoLiveTest() {
        twBot.onChannelGoLive(this.spyChannelGoLiveEvent);
    }


    @Test
    void channelGoOfflineTest() {
        twBot.onChannelViewerCountUpdate(this.spyChannelViewerCountUpdateEvent);
        twBot.onChannelGoOffline(this.spyChannelGoOfflineEvent);
    }
}
