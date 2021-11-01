package ru.hedw1q.TgBot.goodgame;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.hedw1q.TgBot.telegram.TgBot;
import ru.hedw1q.TgBot.twitch.entities.Stream;
import ru.hedw1q.TgBot.twitch.services.StreamService;
import ru.maximkulikov.goodgame.api.models.Channel;
import ru.maximkulikov.goodgame.api.models.ChannelContainer;
import ru.maximkulikov.goodgame.api.models.Game;

import java.util.Collections;

import static ru.hedw1q.TgBot.TgBotApplicationTests.TEST_TELEGRAM_CHANNEL_ID;

/**
 * @author hedw1q
 */
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
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
        Stream currentStream = streamService.getCurrentLiveStreamByChannelName(GGBot.GG_CHANNEL_NAME);
        ggBot.onChannelGoOffline(channelContainerMock,currentStream);
    }

}
