package com.tgerstel.quizmaster;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class QuizMasterApplicationTests {

    @Test
    void contextLoads() {
    }

}
