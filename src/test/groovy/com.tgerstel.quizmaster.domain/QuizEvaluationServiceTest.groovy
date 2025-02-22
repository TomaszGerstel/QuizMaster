package com.tgerstel.quizmaster.domain

import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException
import com.tgerstel.quizmaster.domain.model.EvalAnswer
import com.tgerstel.quizmaster.domain.model.EvalQuestion
import com.tgerstel.quizmaster.domain.model.QuestionSolution
import com.tgerstel.quizmaster.domain.port.QuizEvaluator
import com.tgerstel.quizmaster.domain.port.QuizRepository
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import spock.lang.Specification
import spock.lang.Unroll

@AutoConfigureObservability
class QuizEvaluationServiceTest extends Specification {

    private QuizRepository repository = Mock()
    private QuizEvaluator service = new QuizEvaluationService(repository)

    static QuizEvalDTO quiz1 = new QuizEvalDTO("quiz1Id", List.of(
            new EvalQuestion("q01Id", List.of(new EvalAnswer(1, false), new EvalAnswer(2, true))),
            new EvalQuestion("q02Id", List.of(new EvalAnswer(1, true), new EvalAnswer(2, false)))
    ))

    static QuizEvalDTO quiz2 = new QuizEvalDTO("quiz2Id", List.of(
            new EvalQuestion("q03Id", List.of(new EvalAnswer(1, true), new EvalAnswer(2, false),
                    new EvalAnswer(3, true))),
            new EvalQuestion("q04Id", List.of(new EvalAnswer(1, false), new EvalAnswer(2, true),
                    new EvalAnswer(3, false))),
            new EvalQuestion("q05Id", List.of(new EvalAnswer(1, true), new EvalAnswer(2, false)))
    ))


    @Unroll("should eval properly quiz solution: #description")
    def "should eval properly quiz solution"() {
        given:
        def command = new SubmitQuizCommand(quizData.id(), questionSolutions)
        def quiz = Optional.of(quizData)

        when:
        def result = service.submitQuiz(command)

        then:
        1 * repository.getEvalById(quizData.id()) >> quiz
        result.quizId() == quizData.id()
        result.positive == expPositive
        result.quizScore() == expScore
        result.questionsCount() == expQCount

        where:
        quizData | questionSolutions                                                                                          | expPositive | expScore | expQCount | description
        quiz1    | List.of(solution("q01Id", List.of(2)), solution("q02Id", List.of(1)))                                      | true        | 2        | 2         | "2/2 correct answers"
        quiz1    | List.of(solution("q01Id", List.of(1)), solution("q02Id", List.of(1)))                                      | false       | 1        | 2         | "1/2 correct answers"
        quiz1    | List.of(solution("q01Id", List.of(1)), solution("q02Id", List.of(3)))                                      | false       | 0        | 2         | "0/2 correct answers"
        quiz2    | List.of(solution("q03Id", List.of(1, 3)), solution("q04Id", List.of(2)), solution("q05Id", List.of(1)))    | true        | 3        | 3         | "3/3 with multi answer question"
        quiz2    | List.of(solution("q03Id", List.of(1)), solution("q04Id", List.of(2)), solution("q05Id", List.of(1)))       | true        | 2        | 3         | "2/3 with wrong answer for multi answer question"
        quiz2    | List.of(solution("q04Id", List.of(2)), solution("q05Id", List.of(1)))                                      | true        | 2        | 3         | "2/3 as positive even with missing one answer"
        quiz2    | List.of(solution("q03Id", List.of(1, 3)), solution("q04Id", List.of(2, 3)), solution("q05Id", List.of(6))) | false       | 1        | 3         | "1/3 as negative"
    }

    def "should throw exception for not existing quiz"() {
        given:
        def quizId = "testId"
        def command = new SubmitQuizCommand(quizId, List.of(solution("someId", List.of(2))))
        def quiz = Optional.empty()

        when:
        service.submitQuiz(command)

        then:
        1 * repository.getEvalById(quizId) >> quiz
        thrown(QuizNotFoundException)
    }

    def "should throw exception for not related question to given quiz"() {
        def wrongQuestionId = "q04Id"
        def solutions = List.of(solution("q01Id", List.of(2)), solution(wrongQuestionId, List.of(2)))
        def command = new SubmitQuizCommand(quiz1.id(), solutions)

        when:
        service.submitQuiz(command)

        then:
        1 * repository.getEvalById(quiz1.id()) >> Optional.of(quiz1)
        def exception = thrown(IllegalArgumentException)
        exception.message == "Question with id: $wrongQuestionId not related to the quiz"

    }

    private static QuestionSolution solution(String questionId, List<Integer> answerIds) {
        return new QuestionSolution(questionId, answerIds)
    }

}