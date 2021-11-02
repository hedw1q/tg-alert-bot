package ru.hedw1q.TgBot.twitch.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author hedw1q
 */
@Entity
@Table(name = "STREAMERS", indexes = {
        @Index(name = "idx", columnList = "channelName, platform",unique = true)
})
@EqualsAndHashCode
@ToString
public class Streamer extends BaseEntity {
    public Streamer(String channelName, String platform) {
        this.channelName=channelName;
        this.platform=platform;
    }

    public Streamer(String channelName, String platform,char gender) {
        this.channelName=channelName;
        this.platform=platform;
        this.gender=gender;
    }

    public Streamer(){}

    @NotNull
    @Getter
    @Setter
    private String channelName;

    @NotNull
    @Getter
    @Setter
    private String platform;

    @Getter
    @Setter
    private char gender;
}
