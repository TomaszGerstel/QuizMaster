package com.tgerstel.quizmaster.domain

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand
import com.tgerstel.quizmaster.domain.command.StartQuizCommand
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException
import com.tgerstel.quizmaster.domain.model.Answer
import com.tgerstel.quizmaster.domain.model.Question
import com.tgerstel.quizmaster.domain.port.QuestionRepository
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository
import com.tgerstel.quizmaster.domain.port.QuizManager
import com.tgerstel.quizmaster.domain.port.QuizRepository
import org.bson.types.ObjectId
import spock.lang.Specification

class QuizManagementServiceTest extends Specification {

    private QuizRepository quizRepository = Mock()
    private QuizAttemptRepository attemptRepository = Mock()
    private QuestionRepository questionRepository = Mock()
    private QuizManager quizManagementService = new QuizManagementService(quizRepository, attemptRepository,
            questionRepository)

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
        def quizId = "deadbeefcafebabe12345601"

        def quiz = new QuizToSolveDTO(quizId.toString(), "someTitle",
                null,
                [new Question("qId1", "Some question",
                        [new Answer(1, "some answer"),
                         new Answer(2, "another answer")]),
                 new Question("qId2", "Another question",
                         [new Answer(1, "answer"),
                          new Answer(2, "different answer")])])

        quizRepository.getById(quizId) >> Optional.of(quiz)

        when:
        def result = quizManagementService.startQuiz(new StartQuizCommand(quizId, "anyUser", "anyEmail"))

        then:
        result.id == quizId.toString()
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
        def quizId = "deadbeefcafebabe12999601"
        quizRepository.getById(quizId) >> Optional.empty()

        when:
        quizManagementService.startQuiz(new StartQuizCommand(quizId, "anyUser", "anyEmail"))

        then:
        1 * quizRepository.getById(quizId) >> Optional.empty()
        thrown(QuizNotFoundException)
    }

    def "should start quiz"() {
        given:
        def id = "deadbeefcafebabe12345601"
        def command = new StartQuizCommand(id, 'anyUser', 'anyEmail')

        when:
        def result = quizManagementService.startQuiz(command)

        then:
        1 * quizRepository.getById(id) >> Optional.of(new QuizToSolveDTO(id, "someTitle",
                null,
                [new Question("qId1", "Some question",
                        [new Answer(1, "some answer"),
                         new Answer(2, "another answer")]),
                 new Question("qId2", "Another question",
                         [new Answer(1, "answer"),
                          new Answer(2, "different answer")])
                ]))

        result.id == id
        result.title == "someTitle"
        result.sessionId != null
        result.questions.size() == 2
        result.questions[0].id() == "qId1"
    }


    def "should create quiz"() {
        given:
        def command = new CreateQuizCommand("New Quiz", "user02",
                List.of("qId1", "qId2") as List<ObjectId>)
        def objQuizId = new ObjectId("deadbeefcafebabe12345602")

        when:
        def result = quizManagementService.createQuiz(command)

        then:
        1 * quizRepository.createQuiz(command) >> objQuizId
        result == objQuizId.toString()
    }

    def "should add questions to quiz"() {
        given:
        def quizId = "deadbeefcafebabe12345603"
        def command = new CreateQuizCommand("New Quiz", "user02", List.of())
        def objQuizId = new ObjectId(quizId)

        when:
        quizManagementService.createQuiz(command)
        quizManagementService.assignQuestionsToQuiz(quizId, List.of("qId1", "qId2") as List<String>)

        then:
        1 * quizRepository.createQuiz(command) >> objQuizId
        1 * quizRepository.addQuestionsToQuiz(quizId, List.of("qId1", "qId2") as List<String>)
    }

    def "should remove questions from quiz"() {
        given:
        def quizId = "deadbeefcafebabe12345604"
        def command = new CreateQuizCommand("New Quiz", "user02", List.of())
        def objQuizId = new ObjectId(quizId)

        when:
        quizManagementService.createQuiz(command)
        quizManagementService.removeQuestionsFromQuiz(quizId, List.of("qId1", "qId2") as List<String>)

        then:
        1 * quizRepository.createQuiz(command) >> objQuizId
        1 * quizRepository.removeQuestionsFromQuiz(quizId, List.of("qId1", "qId2") as List<String>)
    }


}