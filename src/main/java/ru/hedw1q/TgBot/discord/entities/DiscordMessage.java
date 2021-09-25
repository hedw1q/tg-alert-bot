package ru.hedw1q.TgBot.discord.entities;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hedw1q
 */
@Data
public class DiscordMessage {
    private String messageText;

    public DiscordMessage(String messageText) {
        this.messageText = messageText;
    }

    public String formatOutputText(Map<String, String> stickerMap) {
        this.removeMentions()
                .replaceDiscordStickersToEmojis(stickerMap);
        return this.getMessageText();
    }

    public String formatOutputText(Map<String, String> stickerMap, String messageAuthor) {
        this.removeMentions()
                .replaceDiscordStickersToEmojis(stickerMap)
                .addMessageAuthor(messageAuthor);
        return this.getMessageText();
    }

    public DiscordMessage addMessageAuthor(String messageAuthor){
        setMessageText(messageAuthor +
                ": "+
                getMessageText()
        );
        return this;
    }

    public DiscordMessage removeMentions() {
// <@&798570807143170079> <@&808698758547374111> @everyone
        Pattern pattern = Pattern.compile("<@.+>");
        Matcher matcher = pattern.matcher(getMessageText());
        while (matcher.find()) {
            setMessageText(
                    StringUtils.remove(getMessageText(),
                            getMessageText().substring(matcher.start(), matcher.end())));
        }
        setMessageText(
                getMessageText()
                        .replace("@everyone","")
                        .replace("@here","")
        );
        return this;
    }

    //<:widepeepoHappy:803962534511771708><a:RamonaSho:837245676923650049>
    public DiscordMessage replaceDiscordStickersToEmojis(Map<String, String> stickerMap) {
        //Pattern <:*текст>
        Pattern pattern = Pattern.compile("<.+?>");
        Matcher matcher = pattern.matcher(getMessageText());
        while (matcher.find()) {
            String stickerFullName = matcher.group();
            Pattern p = Pattern.compile("\\<([^)]+)\\:");
            Matcher m = p.matcher(stickerFullName);
            if (m.find()) {
                String stickerName = m.group(0).replaceAll("<:", "")
                        .replaceAll("<a:", "")
                        .replaceAll(":", "");
                setMessageText(StringUtils.replace(this.messageText, stickerFullName, stickerMap.getOrDefault(stickerName, "")));
            }
        }
        return this;
    }
}
