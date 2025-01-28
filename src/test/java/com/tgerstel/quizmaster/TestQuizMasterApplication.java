package com.tgerstel.quizmaster;

import org.springframework.boot.SpringApplication;

public class TestQuizMasterApplication {

    public static void main(String[] args) {
        SpringApplication.from(QuizMasterApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
