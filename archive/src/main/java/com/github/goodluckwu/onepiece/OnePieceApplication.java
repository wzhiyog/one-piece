package com.github.goodluckwu.onepiece;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.context.WebServerPortFileWriter;

@SpringBootApplication
public class OnePieceApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OnePieceApplication.class);
		app.addListeners(new ApplicationPidFileWriter(), new WebServerPortFileWriter());
		app.run(args);

	}
}
