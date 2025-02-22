package com.tgerstel.quizmaster.adaper.endpoint;

import com.tgerstel.quizmaster.helper.InMemoryQuizRepositoryImpl;
import com.tgerstel.quizmaster.helper.QuizTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QuizControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryQuizRepositoryImpl quizRepository;

    @AfterEach
    public void setUp() {
        quizRepository.clear();
    }

    @Test
    public void testGetAllQuizzes() {
        QuizTestUtils.createAndSaveQuizDocument(quizRepository, "1", "Quiz 1");
        QuizTestUtils.createAndSaveQuizDocument(quizRepository, "2", "Quiz 2");
        given()
                .when()
                .get(baseURI + ":" + port + "/quiz")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", equalTo("1"))
                .body("[0].title", equalTo("Quiz 1"))
                .body("[0].questionsQuantity", equalTo(2))
                .body("[1].id", equalTo("2"))
                .body("[1].title", equalTo("Quiz 2"))
                .body("[1].questionsQuantity", equalTo(2));
    }

    @Test
    public void testGetQuizById() {
        QuizTestUtils.createAndSaveQuizDocument(quizRepository,"1", "Quiz 1");
        QuizTestUtils.createAndSaveQuizDocument(quizRepository,"2", "Quiz 2");
        given()
                .pathParam("id", "2")
                .when()
                .get(baseURI + ":" + port + "/quiz/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo("2"))
                .body("title", equalTo("Quiz 2"))

                .body("questions.size()", is(2))

                .body("questions[0].question", equalTo("What is the capital of France?"))
                .body("questions[0].answers.size()", is(3))
                .body("questions[0].answers[0].content", equalTo("London"))
                .body("questions[0].answers[1].content", equalTo("Paris"))
                .body("questions[0].answers[2].content", equalTo("Madrid"))

                .body("questions[1].question", equalTo("What is the capital of Germany?"))
                .body("questions[1].answers.size()", is(3))
                .body("questions[1].answers[0].content", equalTo("London"))
                .body("questions[1].answers[1].content", equalTo("Berlin"))
                .body("questions[1].answers[2].content", equalTo("Madrid"));
    }

    @Test
    public void testGetQuizByIdShouldReturnNotFoundCode() {
        QuizTestUtils.createAndSaveQuizDocument(quizRepository,"1", "Quiz 1");
        var notExistingId = "2";

        given()
                .pathParam("id", notExistingId)
                .when()
                .get(baseURI + ":" + port + "/quiz/{id}")
                .then()
                .statusCode(404)
                .body("reason", equalTo("Quiz with id " + notExistingId + " not found"));
    }

}
