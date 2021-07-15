package ru.hedw1q.TgBot.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hedw1q
 */
@Controller
@RequestMapping("/")
public class ViewController {
    @GetMapping
    public String pingApp() {
        return "index";
    }
}
