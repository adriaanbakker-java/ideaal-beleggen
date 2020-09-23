package com.example.demo;

import javafx.application.Application;
//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootExampleApplication {

	public static void main(String[] args) {
		// This is how normal Spring Boot app would be launched
		// SpringApplication.run(SpringBootExampleApplication.class, args);

		// JavaFxApplication doesn't exist yet,
		// we'll create it in the next step
		Application.launch(JavaFxApplication.class, args);
	}

}
