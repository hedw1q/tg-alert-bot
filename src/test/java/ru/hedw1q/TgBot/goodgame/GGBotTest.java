package ru.hedw1q.TgBot.goodgame;

import com.github.twitch4j.common.events.domain.EventChannel;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.entities.streamers.Ramona;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.maximkulikov.goodgame.api.handlers.StreamChannelResponseHandler;
import ru.maximkulikov.goodgame.api.models.Channel;
import ru.maximkulikov.goodgame.api.models.ChannelContainer;
import ru.maximkulikov.goodgame.api.models.Game;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static ru.hedw1q.TgBot.TgBotApplicationTests.TEST_TELEGRAM_CHANNEL_ID;

/**
 * @author hedw1q
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GGBotTest {

    @Autowired
    TgBot tgBot;
    @Autowired
    StreamService streamService;
    @Autowired
    GGBot ggBot;
    @Mock
    private ChannelContainer channelContainerMock;

    private static int TEST_STREAM_ID;

    @BeforeEach
    void setUp() {
        GGBot.TG_CHANNEL_ID = TEST_TELEGRAM_CHANNEL_ID;
        GGBot.GG_CHANNEL_NAME ="TestGGStreamer";
        com.github.twitch4j.helix.domain.Stream streamSpy = Mockito.mock(com.github.twitch4j.helix.domain.Stream.class);

        Channel channelSpy=Mockito.mock(Channel.class);
        Mockito.when(channelContainerMock.getChannel()).thenReturn(channelSpy);
        Game testgame=new Game();
        testgame.setTitle("Game");
        Mockito.when(channelContainerMock.getViewers()).thenReturn("228");
        Mockito.when(channelSpy.getGames()).thenReturn(Collections.singletonList(testgame));
        Mockito.when(channelSpy.getTitle()).thenReturn("Title");
        Mockito.when(channelSpy.getThumb()).thenReturn("//cdn.pixabay.com/photo/2013/07/12/17/47/test-pattern-152459_960_720.png");
    }

    @Test
    @Order(1)
    void channelAliveTest(){
        ggBot.onChannelGoLive(channelContainerMock);
    }

    @Test
    @Order(2)
    void channelOfflineTest(){
        Stream currentStream = streamService.getLastStreamByChannelName(GGBot.GG_CHANNEL_NAME);
        TEST_STREAM_ID=currentStream.getId();
        ggBot.onChannelGoOffline(channelContainerMock,currentStream);
    }

    @AfterAll
    void tearDown() {
        streamService.deleteStreamById(TEST_STREAM_ID);
    }
}
