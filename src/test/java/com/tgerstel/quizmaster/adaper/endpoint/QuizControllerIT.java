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

    @Autowired
    private InMemoryQuizRepositoryImpl attemptRepository;


    @AfterEach
    public void setUp() {
        quizRepository.clear();
        attemptRepository.clear();
    }

    @Test
    public void testGetAllQuizzes() {
        QuizTestUtils.createAndSaveQuiz(quizRepository, "507f1f77bcf86cd799439011", "Quiz 1");
        QuizTestUtils.createAndSaveQuiz(quizRepository, "507f1f77bcf86cd799439012", "Quiz 2");
        given()
                .when()
                .get(baseURI + ":" + port + "/api/quiz")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].id", equalTo("507f1f77bcf86cd799439011"))
                .body("[0].title", equalTo("Quiz 1"))
                .body("[0].questionsQuantity", equalTo(2))
                .body("[1].id", equalTo("507f1f77bcf86cd799439012"))
                .body("[1].title", equalTo("Quiz 2"))
                .body("[1].questionsQuantity", equalTo(2));
    }

    @Test
    public void testGetQuizById() {
        QuizTestUtils.createAndSaveQuiz(quizRepository,"507f1f77bcf86cd799439011", "Quiz 1");
        QuizTestUtils.createAndSaveQuiz(quizRepository, "507f1f77bcf86cd799439012", "Quiz 2");
        var requestBody = """
                {
                    "quizId": "507f1f77bcf86cd799439012",
                    "name": "John Doe",
                    "email": "john.doe@email"
                }
                """;
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/api/quiz/start")
                .then()
                .statusCode(200)
                .body("id", equalTo("507f1f77bcf86cd799439012"))
                .body("title", equalTo("Quiz 2"))
                .body("sessionId", notNullValue())
                .body("questions.size()", is(2))
                .body("questions.question", hasItems("What is the capital of France?", "What is the capital of Germany?"))
                .body("questions.find { it.question == 'What is the capital of France?' }.answers.content", hasItems("London", "Paris", "Madrid"))
                .body("questions.find { it.question == 'What is the capital of Germany?' }.answers.content", hasItems("London", "Berlin", "Madrid"));
    }

    @Test
    public void testGetQuizByIdShouldReturnNotFoundCode() {
        QuizTestUtils.createAndSaveQuiz(quizRepository,"507f1f77bcf86cd799439011", "Quiz 1");
        var notExistingId = "507f1f77bcf86cd799439099";
        var requestBody = """
                {
                    "quizId": "%s",
                    "name": "John Doe",
                    "email": "john.doe@email"
                }
                """.formatted(notExistingId);

        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/api/quiz/start")
                .then()
                .statusCode(404)
                .body("reason", equalTo("Quiz with ID " + notExistingId + " not found"));
    }

}
