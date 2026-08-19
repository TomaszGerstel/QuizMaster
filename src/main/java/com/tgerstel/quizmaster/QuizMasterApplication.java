package com.tgerstel.quizmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuizMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizMasterApplication.class, args);
    }

}
