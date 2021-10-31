package ru.hedw1q.TgBot.discord.entities;

import org.junit.jupiter.api.Test;
import ru.hedw1q.TgBot.discord.DsBotInitializer;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * @author hedw1q
 */
public class DiscordMessageTest {

    @Test
    void testRemoveMentions(){
        DiscordMessage msg=new DiscordMessage("@everyone @here hi its message");
        msg=msg.removeMentions();

        assertThat(msg.getMessageText().contains("@everyone")).isFalse();
        assertThat(msg.getMessageText().contains("@here")).isFalse();
    }

    @Test
    void testReplaceStaticDiscordStickersToEmojis(){
        DiscordMessage msg=new DiscordMessage("@everyone  Привет, сегодня стримчик в 17-00 по мск <:honeyr1WOW:803962534511771708>");
        msg=msg.replaceDiscordStickersToEmojis(DsBotInitializer.stickerMap);

        assertThat(msg.getMessageText().trim().endsWith("\uD83D\uDE0A"));
    }

    @Test
    void testReplaceAnimatedDiscordStickersToEmojis(){
        DiscordMessage msg=new DiscordMessage("@everyone  Привет, сегодня стримчик в 17-00 по мск <a:peepoLove:837245676923650049>");
        msg=msg.replaceDiscordStickersToEmojis(DsBotInitializer.stickerMap);

        assertThat(msg.getMessageText().trim().endsWith("❤"));
    }

    @Test
    void testReplaceDiscordStickersToEmpty(){
        DiscordMessage msg=new DiscordMessage("@everyone  Привет, сегодня стримчик в 17-00 по мск <a:123123123123123123:837245676923650049>");
        msg=msg.replaceDiscordStickersToEmojis(DsBotInitializer.stickerMap);

        assertThat(msg.getMessageText().trim().endsWith("мск"));
    }
    @Test
    void testAddMessageAuthor(){
        DiscordMessage msg=new DiscordMessage("msg test");
        msg=msg.addMessageAuthor("hedw1q");
        assertThat(msg.getMessageText().startsWith("From hedw1q:"));
    }
}
