package com.tgerstel.quizmaster.domain

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO
import com.tgerstel.quizmaster.domain.dto.QuizDTO
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException
import com.tgerstel.quizmaster.domain.model.Answer
import com.tgerstel.quizmaster.domain.model.Question
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository
import com.tgerstel.quizmaster.domain.port.QuizManager
import com.tgerstel.quizmaster.domain.port.QuizRepository
import spock.lang.Specification

class QuizManagementServiceTest extends Specification {

    private QuizRepository quizRepository = Mock()
    private QuizAttemptRepository attemptRepository = Mock()
    private QuizManager quizManagementService = new QuizManagementService(quizRepository, attemptRepository)

    def "should return all quizzes"() {
        given:
        def quizzes = [new QuizBasicDTO("qId1", "quiz1", 10),
                       new QuizBasicDTO("qId2", "quiz2", 20)]
        quizRepository.getAll() >> quizzes

        when:
        def result = quizManagementService.getAllQuizzes()

        then:
        result.size() == 2
        result[0].id == "qId1"
        result[0].questionsQuantity == 10
        result[0].title == "quiz1"
        result[1].id == "qId2"
        result[1].questionsQuantity == 20
        result[1].title == "quiz2"
    }

    def "should return quiz by id"() {
        given:
        def quizId = "quiz11"

        def quiz = new QuizDTO(
                quizId, "someTitle",
                null,
                [
                        new Question("qId1", "Some question",
                                [
                                        new Answer(1, "some answer"),
                                        new Answer(2, "another answer")]),
                        new Question("qId2", "Another question",
                                [
                                        new Answer(1, "answer"),
                                        new Answer(2, "different answer")])
                ]
        )

        quizRepository.getById(quizId) >> Optional.of(quiz)

        when:
        def result = quizManagementService.getQuiz(quizId)

        then:
        result.id == quizId
        result.title == "someTitle"
        result.sessionId != null
        result.questions.size() == 2
        result.questions[0].id() == "qId1"
        result.questions[0].question() == "Some question"
        result.questions[0].answers().size() == 2
        result.questions[0].answers()[0].no() == 1
        result.questions[0].answers()[0].content() == "some answer"
        result.questions[0].answers()[1].no() == 2
        result.questions[0].answers()[1].content() == "another answer"
    }

    def "should throw exception when quiz not found"() {
        given:
        def quizId = "nonExistingQuiz"
        quizRepository.getById(quizId) >> Optional.empty()

        when:
        quizManagementService.getQuiz(quizId)

        then:
        1 * quizRepository.getById(quizId) >> Optional.empty()
        thrown(QuizNotFoundException)
    }
}