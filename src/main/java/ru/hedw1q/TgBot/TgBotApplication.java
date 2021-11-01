package ru.hedw1q.TgBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.hedw1q.TgBot.twitch.entities.streamers.BaseTwitchStreamer;
import ru.hedw1q.TgBot.twitch.entities.streamers.Ramona;

@SpringBootApplication
@Controller
@EnableScheduling
public class TgBotApplication {

    @GetMapping("/")
    @ResponseBody
    String pingApp(Model model) {
        return "Hey!";
    }

    public static void main(String[] args) {
        SpringApplication.run(TgBotApplication.class, args);
    }
}
