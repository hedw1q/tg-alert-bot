package ru.hedw1q.TgBot.twitch.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hedw1q
 */
@AllArgsConstructor
@Data
public abstract class Streamer {
    private String oAuthToken;
    private String clientId;
    private String clientSecret;
    private String botNickname;
    private String channelName;
    private String teamName;
    private char sex;
}
