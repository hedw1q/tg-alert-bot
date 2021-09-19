package ru.hedw1q.TgBot.twitch.config;

import lombok.Getter;

/**
 * @author hedw1q
 */
public class AuthData {
    @Getter
    final String oAuthToken;
    @Getter
    final String clientId;
    @Getter
    final String clientSecret;

    public AuthData(String oAuthToken, String clientId, String clientSecret) {
        this.oAuthToken = oAuthToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

}
