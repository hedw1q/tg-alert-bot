package ru.hedw1q.TgBot.twitch.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
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
public class Stream extends BaseEntity {

   public Stream(String channelName,LocalDateTime streamStartTime){
       this.channelName=channelName;
       this.streamStartTime=streamStartTime;
       this.streamStatus="LIVE";
    }
    @NotNull
    @Getter
    @Setter
    private String channelName;

   @Getter
   @Setter
   @NotBlank
   private String streamStatus;

    @NotNull
    @Getter
    @Setter
    private LocalDateTime streamStartTime;

    @Getter
    @Setter
    private LocalDateTime streamFinishTime;
}
