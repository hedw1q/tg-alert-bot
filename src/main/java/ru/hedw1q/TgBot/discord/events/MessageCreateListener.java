package ru.hedw1q.TgBot.discord.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hedw1q.TgBot.discord.entities.DiscordMessage;

import java.util.ArrayList;
import java.util.List;

import static ru.hedw1q.TgBot.discord.DsBotInitializer.stickerMap;

/**
 * @author hedw1q
 */
@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    static final long TG_CHANNEL_ID = -1001537091172L;
    static final long DS_CHANNEL_ID = 552454831122546699L;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        if (event.getMessage().getChannelId().asLong() != DS_CHANNEL_ID || event.getMessage().getAuthor().orElseThrow().isBot()) {
            return Mono.empty();
        }

        DiscordMessage discordMessage = new DiscordMessage(event.getMessage().getContent());
        discordMessage.formatOutputText(stickerMap);

        if (!event.getMessage().getAttachments().isEmpty()) {
            List<Attachment> attachmentList = new ArrayList<>(event.getMessage().getAttachments());
            attachmentList.stream()
                    .map(Attachment::getUrl)
                    .forEach(attachmentUrl -> tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, attachmentUrl, discordMessage.getMessageText()));
            return Mono.empty();
        }

        tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, discordMessage.getMessageText());

        return Mono.empty();
    }


}