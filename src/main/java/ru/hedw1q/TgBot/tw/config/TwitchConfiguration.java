package ru.hedw1q.TgBot.tw.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hedw1q
 */

@Data
@AllArgsConstructor
public class TwitchConfiguration {
    private String oAuthToken;
    private String clientId;
    private String clientSecret;
    private String botNickname;

    private String channelName;
    private String teamName;
}
