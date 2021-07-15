package ru.hedw1q.TgBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class TgBotApplication {

	@RequestMapping("/")
	@ResponseBody
	String pingApp() {
		return "Hello World!";
	}

	public static void main(String[] args){
			SpringApplication.run(TgBotApplication.class, args);
	}
}
