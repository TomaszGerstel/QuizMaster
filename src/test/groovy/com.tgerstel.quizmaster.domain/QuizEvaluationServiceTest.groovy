package com.tgerstel.quizmaster.domain

import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand
import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException
import com.tgerstel.quizmaster.domain.model.AnswerReportEntry
import com.tgerstel.quizmaster.domain.model.EvalAnswer
import com.tgerstel.quizmaster.domain.model.EvalQuestion
import com.tgerstel.quizmaster.domain.model.QuestionSolution
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository
import com.tgerstel.quizmaster.domain.port.QuizEvaluator
import com.tgerstel.quizmaster.domain.port.QuizRepository
import org.bson.types.ObjectId
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

@AutoConfigureObservability
class QuizEvaluationServiceTest extends Specification {

    private QuizRepository quizRepository = Mock()
    private QuizAttemptRepository attemptRepository = Mock()
    private QuizEvaluator service = new QuizEvaluationService(quizRepository, attemptRepository)

    static String quiz1id = "deadbeefcafebabe12345601"
    static String quiz2id = "deadbeefcafebabe12345602"

    static String question1id = "cafebeefdeadbead12345001"
    static String question2id = "cafebeefdeadbead12345002"
    static String question3id = "cafebeefdeadbead12345003"
    static String question4id = "cafebeefdeadbead12345004"
    static String question5id = "cafebeefdeadbead12345005"

    static QuizEvalDTO quiz1 = new QuizEvalDTO(quiz1id, List.of(
            new EvalQuestion(question1id, "some explanation", List.of(new EvalAnswer(1, false), new EvalAnswer(2, true)), "user1"),
            new EvalQuestion(question2id, "some explanation 2", List.of(new EvalAnswer(1, true), new EvalAnswer(2, false)), "user2")
    ))

    static QuizEvalDTO quiz2 = new QuizEvalDTO(quiz2id, List.of(
            new EvalQuestion(question3id, "explanation 3", List.of(new EvalAnswer(1, true), new EvalAnswer(2, false),
                    new EvalAnswer(3, true)), "user3"),
            new EvalQuestion(question4id, "", List.of(new EvalAnswer(1, false), new EvalAnswer(2, true),
                    new EvalAnswer(3, false)), "user3"),
            new EvalQuestion(question5id, "", List.of(new EvalAnswer(1, true), new EvalAnswer(2, false)), "user3")
    ))

    static QuizAttemptDTO attemptDTO =
            new QuizAttemptDTO("session123", quiz1id, "user1", "user1@email", Instant.now().minusSeconds(60), Instant.now(), 12, 10)


    @Unroll("should eval properly quiz solution: #description")
    def "should eval properly quiz solution"() {
        given:
        def sessionId = "session123"
        def command = new SubmitQuizCommand(new ObjectId(quizData.id()), questionSolutions, sessionId)
        def quiz = Optional.of(quizData)

        when:
        def result = service.submitQuiz(command)

        then:
        1 * quizRepository.getEvalById(new ObjectId(quizData.id())) >> quiz
        1 * attemptRepository.getAndEnd(sessionId, _ as Instant, _ as int) >> Optional.of(attemptDTO)
        result.quizId() == quizData.id()
        result.positive == expPositive
        result.quizScore() == expScore
        result.questionsCount() == expQCount
        result.answersReport() == expAnswerReport
        result.attemptTimeInSeconds() != null

        where:
        quizData | questionSolutions                                                                                                                               | expPositive | expScore | expQCount | expAnswerReport                                                                                                                                  | description
        quiz1    | List.of(solution(question1id, "some explanation", List.of(2)), solution(question2id, "some explanation 2", List.of(1)))                         | true        | 2        | 2         | [answerReport(question1id, [2], "some explanation", true), answerReport(question2id, [1], "some explanation 2", true)]                           | "2/2 correct answers"
        quiz1    | List.of(solution(question1id, "some explanation", List.of(1)), solution(question2id, "some explanation 2", List.of(1)))                         | false       | 1        | 2         | [answerReport(question1id, [2], "some explanation", false), answerReport(question2id, [1], "some explanation 2", true)]                          | "1/2 correct answers"
        quiz1    | List.of(solution(question1id, "some explanation", List.of(1)), solution(question2id, "exp2", List.of(3)))                                       | false       | 0        | 2         | [answerReport(question1id, [2], "some explanation", false), answerReport(question2id, [1], "some explanation 2", false)]                         | "0/2 correct answers"
        quiz2    | List.of(solution(question3id, "explanation 3", List.of(1, 3)), solution(question4id, "", List.of(2)), solution(question5id, "", List.of(1)))    | true        | 3        | 3         | [answerReport(question3id, [1, 3], "explanation 3", true), answerReport(question4id, [2], "", true), answerReport(question5id, [1], "", true)]   | "3/3 with multi answer question"
        quiz2    | List.of(solution(question3id, "explanation 3", List.of(1)), solution(question4id, "", List.of(2)), solution(question5id, "", List.of(1)))       | true        | 2        | 3         | [answerReport(question3id, [1, 3], "explanation 3", false), answerReport(question4id, [2], "", true), answerReport(question5id, [1], "", true)]  | "2/3 with wrong answer for multi answer question"
        quiz2    | List.of(solution(question4id, "", List.of(2)), solution(question5id, "", List.of(1)))                                                           | true        | 2        | 3         | [answerReport(question3id, [1, 3], "explanation 3", false), answerReport(question4id, [2], "", true), answerReport(question5id, [1], "", true)]  | "2/3 as positive even with missing one answer"
        quiz2    | List.of(solution(question3id, "explanation 3", List.of(1, 3)), solution(question4id, "", List.of(2, 3)), solution(question5id, "", List.of(6))) | false       | 1        | 3         | [answerReport(question3id, [1, 3], "explanation 3", true), answerReport(question4id, [2], "", false), answerReport(question5id, [1], "", false)] | "1/3 as negative"
    }

    def "should throw exception for not existing quiz"() {
        given:
        def quizId = new ObjectId(quiz2id)
        def sessionId = "session123"
        def command = new SubmitQuizCommand(quizId, List.of(solution("deadbeefcafebabe12345901", "exp", List.of(2))), sessionId)
        def quiz = Optional.empty()

        when:
        service.submitQuiz(command)

        then:
        1 * quizRepository.getEvalById(quizId) >> quiz
        thrown(QuizNotFoundException)
    }

    def "should throw exception for not related question to given quiz"() {
        def wrongQuestionId = "deadbeefcafebabe12345999"
        def sessionId = "session123"
        def solutions = List.of(solution("cafebeefdeadbead12345001", "exc", List.of(2)), solution(wrongQuestionId, "", List.of(2)))
        def command = new SubmitQuizCommand(new ObjectId(quiz1.id()), solutions, sessionId)

        when:
        service.submitQuiz(command)

        then:
        1 * quizRepository.getEvalById(new ObjectId(quiz1.id())) >> Optional.of(quiz1)
        def exception = thrown(IllegalArgumentException)
        exception.message == "Question with id: $wrongQuestionId not related to the quiz"

    }

    private static QuestionSolution solution(String questionId, String exp, List<Integer> answerIds) {
        return new QuestionSolution(new ObjectId(questionId), exp, answerIds)
    }

    private static AnswerReportEntry answerReport(String questionId, List<Integer> expectedAnswers, String exp, boolean positive) {
        return new AnswerReportEntry(questionId, expectedAnswers as Set, exp, positive)
    }

}