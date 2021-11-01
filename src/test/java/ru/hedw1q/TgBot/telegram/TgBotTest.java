package ru.hedw1q.TgBot.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.hedw1q.TgBot.TgBotApplicationTests.TEST_TELEGRAM_CHANNEL_ID;

/**
 * @author hedw1q
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TgBotTestConfig.class)
public class TgBotTest {
    @Autowired
    TgBot tgBot;
    String testmsg = "Test Message!;!@#!@# \uD83D\uDE34";


    @Test
    void sendTextMessageToChannelTest() {
        Message msg = tgBot.sendTextMessageToChannel(TEST_TELEGRAM_CHANNEL_ID, testmsg);


        assertThat(msg.getMessageId()).isNotNull();
        assertThat(msg.getChatId()).isEqualTo(TEST_TELEGRAM_CHANNEL_ID);
        assertThat(msg.getText()).isEqualTo(testmsg);
    }

    @Test
    void sendTextUrlMessageToChannelTest() {
        final String msgUrl = "ТЕСТОВОЕ СООБЩЕНИЕ!\n@everyone Ну получается тогда завожу тест, ПОИНТОВЫЙ АУК на игры и the last spell\n" +
                "\n" +
                "https://www.twitch.tv/krabick\n" +
                "https://www.twitch.tv/krabick\n" +
                "https://www.twitch.tv/krabick \n" +
                "\n" +
                "memelord: Ankoreus\n" +
                "Буду вас ждать\n" +
                "\n" +
                "\n" +
                "https://media.discordapp.net/attachments/600450906877591583/711626829227884614/13160c5543430f30.png?width=880&height=495";

        Message msg = tgBot.sendTextMessageToChannel(TEST_TELEGRAM_CHANNEL_ID, msgUrl);

        assertThat(msg.getChatId()).isEqualTo(TEST_TELEGRAM_CHANNEL_ID);
        assertThat(msg.getText()).isEqualTo(msgUrl);
    }

    @Test
    void sendAttachmentMessageToChannelTest() {
        final String jpgUrl = "https://www.massagebythesea.com.au/wp-content/uploads/2018/12/Test-JPEG-1-300x150.jpg";
        final String pngUrl = "https://www.massagebythesea.com.au/wp-content/uploads/2018/12/Test-Logo.svg.png";
        final String gifUrl = "https://www.latitudefestival.com/wp-content/uploads/2019/04/test.gif";

        try {
            Message jpgMsg = tgBot.sendAttachmentMessageToChannel(TEST_TELEGRAM_CHANNEL_ID, jpgUrl, testmsg);
            TimeUnit.SECONDS.sleep(1);
            Message pngMsg = tgBot.sendAttachmentMessageToChannel(TEST_TELEGRAM_CHANNEL_ID, pngUrl, testmsg);
            TimeUnit.SECONDS.sleep(1);
            Message gifMsg = tgBot.sendAttachmentMessageToChannel(TEST_TELEGRAM_CHANNEL_ID, gifUrl, testmsg);

            assertThat(jpgMsg.getChatId()).isEqualTo(TEST_TELEGRAM_CHANNEL_ID);

            assertThat(jpgMsg.getCaption()).isEqualTo(testmsg);
            assertThat(jpgMsg.getPhoto().get(0).getFileUniqueId()).isNotEmpty();

            assertThat(pngMsg.getCaption()).isEqualTo(testmsg);
            assertThat(pngMsg.getPhoto().get(0).getFileUniqueId()).isNotEmpty();

            assertThat(gifMsg.getCaption()).isEqualTo(testmsg);
            assertThat(gifMsg.getVideo().getFileUniqueId()).isNotEmpty();
        } catch (InterruptedException ie) {
        }
    }
}
