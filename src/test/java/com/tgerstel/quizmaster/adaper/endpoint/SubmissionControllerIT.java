package com.tgerstel.quizmaster.adaper.endpoint;

import com.tgerstel.quizmaster.helper.InMemoryQuizAttemptRepository;
import com.tgerstel.quizmaster.helper.InMemoryQuizRepositoryImpl;
import com.tgerstel.quizmaster.helper.QuizTestUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SubmissionControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryQuizRepositoryImpl quizRepository;

    @Autowired
    private InMemoryQuizAttemptRepository attemptRepository;

    @AfterEach
    public void setUp() {
        quizRepository.clear();
        attemptRepository.clear();
    }

    @Test
    public void testSubmitQuiz() {
        var sessionId = "session997";
        var quizId = new ObjectId("deadbeefcafebabe12345601");
        var userName = "John Doe";
        var email = "john1@email";
        QuizTestUtils.createAndSaveQuizDocument(quizRepository, quizId, "Quiz 1");
        QuizTestUtils.createAndSaveQuizAttempt(attemptRepository, quizId, 8, sessionId, userName, email);
        var requestBody = """
                {
                    "quizId": "deadbeefcafebabe12345601",
                    "sessionId": "%s",
                    "solutions": [
                        {
                            "questionId": "507f1f77bcf86cd799439012",
                            "answers": [ 2 ]
                        },
                        {
                            "questionId": "507f1f77bcf86cd799439011",
                            "answers": [ 2 ]
                        }
                    ]
                }
                """.formatted(sessionId);
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/api/submission")
                .then()
                .statusCode(202)
                .body("quizId", equalTo("deadbeefcafebabe12345601"))
                .body("quizScore", equalTo(2))
                .body("percentageScore", equalTo(100))
                .body("isPositive", equalTo(true))
                .body("questionsCount", equalTo(2))
                .body("attemptTimeInSeconds", notNullValue())
                .body("answersReport.size()", equalTo(2))
                .body("answersReport[0].questionId", equalTo("507f1f77bcf86cd799439012"))
                .body("answersReport[0].expectedAnswers", hasSize(1))
                .body("answersReport[0].expectedAnswers[0]", equalTo(2))
                .body("answersReport[0].positive", equalTo(true))
                .body("answersReport[0].explanation", notNullValue())
                .body("answersReport[1].questionId", equalTo("507f1f77bcf86cd799439011"))
                .body("answersReport[1].expectedAnswers", hasSize(1))
                .body("answersReport[1].expectedAnswers[0]", equalTo(2))
                .body("answersReport[1].positive", equalTo(true))
                .body("answersReport[1].explanation", notNullValue());
    }

    @Test
    public void testSubmitQuizShouldReturnBadRequest() {
        QuizTestUtils.createAndSaveQuizDocument(quizRepository, new ObjectId("deadbeefcafebabe12345601"), "Quiz 1");
        var notExistingQuestionIdInQuiz = "507f1f77bcf86cd799439333";
        var requestBody = """
                {
                    "quizId": "deadbeefcafebabe12345601",
                    "solutions": [
                        {
                            "questionId": "507f1f77bcf86cd799439012",
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
                .post(baseURI + ":" + port + "/api/submission")
                .then()
                .statusCode(400)
                .body("reason", equalTo("Question with id: %s not related to the quiz"
                        .formatted(notExistingQuestionIdInQuiz)));
    }

    @Test
    public void testSubmitQuizShouldReturnNotFound() {
        var notExistingQuizId = "deadbeefcafebabe12347777";
        var requestBody = """
                {
                    "quizId": "%s",
                    "solutions": [
                        {
                            "questionId": "507f1f77bcf86cd799439012",
                            "answers": [ 2 ]
                        },
                        {
                            "questionId": "507f1f77bcf86cd799439011",
                            "answers": [ 2 ]
                        }
                    ]
                }
                """.formatted(notExistingQuizId);
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when()
                .post(baseURI + ":" + port + "/api/submission")
                .then()
                .statusCode(404)
                .body("reason", equalTo("Quiz with ID %s not found".formatted(notExistingQuizId)));
    }
}
