package ru.hedw1q.TgBot.discord;

import discord4j.core.object.entity.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.hedw1q.TgBot.discord.events.MessageCreateListener;
import ru.hedw1q.TgBot.telegram.TgBotTestConfig;

/**
 * @author hedw1q
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DsBotTestConfig.class)
public class DsBotTest {
    @Autowired
    private DsBotInitializer dsBotInitializer;

    @Test
    void testMessageCreate(){

    }
}
