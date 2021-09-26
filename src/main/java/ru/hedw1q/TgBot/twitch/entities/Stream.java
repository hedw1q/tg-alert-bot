package ru.hedw1q.TgBot.twitch.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author hedw1q
 */
@Entity
@Table(name = "STREAMS")
@ToString
public class Stream extends BaseEntity {
    public Stream() {}

    public Stream(String channelName, LocalDateTime streamStartTime, String platform) {
        this.channelName = channelName;
        this.streamStartTime = streamStartTime;
        this.streamStatus = StreamStatus.LIVE;
        this.platform=platform;
    }

    @NotNull
    @Getter
    @Setter
    private String channelName;

    @Getter
    @Setter
    @NotBlank
    @Enumerated(EnumType.STRING)
    private StreamStatus streamStatus;

    @NotNull
    @Getter
    @Setter
    private LocalDateTime streamStartTime;

    @Getter
    @Setter
    private LocalDateTime streamFinishTime;


    @Getter
    @Setter
    private String platform;
}
