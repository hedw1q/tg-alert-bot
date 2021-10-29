package ru.hedw1q.TgBot.discord.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hedw1q.TgBot.discord.entities.DiscordMessage;

import java.util.ArrayList;
import java.util.List;

import static ru.hedw1q.TgBot.discord.DsBotInitializer.stickerMap;
import static ru.hedw1q.TgBot.discord.events.MessageCreateListener.*;

/**
 * @author hedw1q
 */
//@Service
public class MessageUpdateListener extends MessageListener implements EventListener<MessageUpdateEvent> {

    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
        if (!validateMessage(event)) {
            return Mono.empty();
        }
        DiscordMessage discordMessage = new DiscordMessage("UPD: "+event.getMessage().block().getContent());
        discordMessage.formatOutputText(stickerMap);

        if (!event.getMessage().block().getAttachments().isEmpty()) {
            List<Attachment> attachmentList = new ArrayList<>(event.getMessage().block().getAttachments());
            attachmentList.stream()
                    .map(Attachment::getUrl)
                    .forEach(attachmentUrl -> tgBot.sendAttachmentMessageToChannel(TG_CHANNEL_ID, attachmentUrl, discordMessage.getMessageText()));
            return Mono.empty();
        }

        tgBot.sendTextMessageToChannel(TG_CHANNEL_ID, discordMessage.getMessageText());
        return Mono.empty();
    }


    private boolean validateMessage(MessageUpdateEvent event) {
        if (!DS_CHANNEL_IDS.contains(event.getMessage().block().getChannelId().asLong())
                || event.getMessage().block().getAuthor().orElseThrow().isBot())
            return false;
        return true;
    }
}
