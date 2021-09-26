package ru.hedw1q.TgBot.twitch.entities;

/**
 * @author hedw1q
 */
public enum StreamStatus {
    LIVE("Live"),
    OFFLINE("Offline"),
    UNDERFINED("Underfined");

    private String stringStatus;

    StreamStatus(String str) {
        this.stringStatus = str;
    }

    @Override
    public String toString() {
        return stringStatus;
    }
}
