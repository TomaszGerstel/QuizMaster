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
public class SubmissionControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryQuizRepositoryImpl quizRepository;

    @AfterEach
    public void setUp() {
        quizRepository.clear();
    }

    @Test
    public void testSubmitQuiz() {
        QuizTestUtils.createAndSaveQuizDocument(quizRepository, "1", "Quiz 1");
        var requestBody = """
                {
                    "quizId": "1",
                    "solutions": [
                        {
                            "questionId": "1",
                            "answers": [ 2 ]
                        },
                        {
                            "questionId": "2",
                            "answers": [ 2 ]
                        }
                    ]
                }
                """;
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/submission")
                .then()
                .statusCode(202)
                .body("quizId", equalTo("1"))
                .body("quizScore", equalTo(2))
                .body("isPositive", equalTo(true));
    }

    @Test
    public void testSubmitQuizShouldReturnBadRequest() {
        QuizTestUtils.createAndSaveQuizDocument(quizRepository, "1", "Quiz 1");
        var notExistingQuestionIdInQuiz = "333";
        var requestBody = """
                {
                    "quizId": "1",
                    "solutions": [
                        {
                            "questionId": "1",
                            "answers": [ 2 ]
                        },
                        {
                            "questionId": "%s",
                            "answers": [ 2 ]
                        }
                    ]
                }
                """.formatted(notExistingQuestionIdInQuiz);
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/submission")
                .then()
                .statusCode(400)
                .body("reason", equalTo("Question with id: %s not related to the quiz"
                        .formatted(notExistingQuestionIdInQuiz)));
    }

    @Test
    public void testSubmitQuizShouldReturnNotFound() {
        var notExistingQuizId = "2";
        var requestBody = """
                {
                    "quizId": "%s",
                    "solutions": [
                        {
                            "questionId": "1",
                            "answers": [ 2 ]
                        },
                        {
                            "questionId": "2",
                            "answers": [ 2 ]
                        }
                    ]
                }
                """.formatted(notExistingQuizId);
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/submission")
                .then()
                .statusCode(404)
                .body("reason", equalTo("Quiz with id %s not found".formatted(notExistingQuizId)));
    }
}
