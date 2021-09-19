package ru.hedw1q.TgBot.twitch.config;

/**
 * @author hedw1q
 */
public class AuthData {
    final String oAuthToken;
    final String clientId;
    final String clentSecret;

    public AuthData(String oAuthToken, String clientId, String clentSecret) {
        this.oAuthToken = oAuthToken;
        this.clientId = clientId;
        this.clentSecret = clentSecret;
    }
}
