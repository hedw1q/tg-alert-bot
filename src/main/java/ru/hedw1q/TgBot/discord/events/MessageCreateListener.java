package ru.hedw1q.TgBot.discord.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hedw1q.TgBot.discord.entities.DiscordMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.hedw1q.TgBot.discord.DsBotInitializer.stickerMap;

/**
 * @author hedw1q
 */
//@Service
public class MessageCreateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    static final long TG_CHANNEL_ID = -1001537091172L;
    static final List<Long> DS_CHANNEL_IDS = Arrays.asList(552454831122546699L, 379833575945797633L, 888723970889764868L);

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        if (!validateMessage(event)) {
            return Mono.empty();
        }


        DiscordMessage discordMessage = new DiscordMessage(event.getMessage().getContent());
        discordMessage.formatOutputText(stickerMap,event.getMessage().getAuthor().orElseThrow().getUsername());

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

    private boolean validateMessage(MessageCreateEvent event) {
        if (!DS_CHANNEL_IDS.contains(event.getMessage().getChannelId().asLong())
                || event.getMessage().getAuthor().orElseThrow().isBot())
            return false;
        return true;
    }


}