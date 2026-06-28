package com.tgerstel.quizmaster.domain

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand
import com.tgerstel.quizmaster.domain.command.StartQuizCommand
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException
import com.tgerstel.quizmaster.domain.model.Answer
import com.tgerstel.quizmaster.domain.model.Question
import com.tgerstel.quizmaster.domain.model.Quiz
import com.tgerstel.quizmaster.domain.port.QuestionRepository
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository
import com.tgerstel.quizmaster.domain.port.QuizManager
import com.tgerstel.quizmaster.domain.port.QuizRepository
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
        var filter = QuizFilter.assignable()
        quizRepository.getAllQuizzesForVisibilityAndStatus(filter.visibilities(), filter.statuses()) >> quizzes

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
        var filter = QuizFilter.assignable()

        def quiz = new Quiz(
                quizId.toString(),
                "someTitle",
                "desc",
                "tester1",
                null,
                1L,
                [new Question("id1", "qId1", "Some question", "expl", "tags", "tester1",
                        Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                        Question.Status.PUBLISHED, Question.Visibility.PUBLIC,
                        [new Answer(1, "some answer", false),
                         new Answer(2, "another answer", true)], 1L),
                 new Question("id2", "qId2", "Some question", "expl", "tags", "tester1",
                         Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                         Question.Status.PUBLISHED, Question.Visibility.PUBLIC,
                         [new Answer(1, "answer", true),
                          new Answer(2, "different answer", false)], 1L)],
                Quiz.Type.EXAM,
                Quiz.Status.PUBLISHED,
                Quiz.Visibility.PUBLIC,
                70, true, null, null, null)

        quizRepository.getById(quizId, filter.visibilities(), filter.statuses()) >> Optional.of(quiz)

        when:
        def result = quizManagementService.startQuiz(new StartQuizCommand(quizId, "anyUser", "anyEmail"))

        then:
        result.title == "someTitle"
        result.sessionId != null
        result.questions.size() == 2
        result.questions[0].questionId() == "qId1"
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
        quizRepository.getById(quizId, _ as EnumSet<Quiz.Visibility>, _ as EnumSet<Quiz.Status>) >> Optional.empty()
        var filter = QuizFilter.assignable()

        when:
        quizManagementService.startQuiz(new StartQuizCommand(quizId, "anyUser", "anyEmail"))

        then:
//        1 * attemptRepository.create(_)
        1 * quizRepository.getById(quizId, filter.visibilities(), filter.statuses()) >> Optional.empty()
        thrown(QuizNotFoundException)
    }

    def "should start quiz"() {
        given:
        def id = "deadbeefcafebabe12345601"
        def command = new StartQuizCommand(id, 'anyUser', 'anyEmail')
        var filter = QuizFilter.assignable()

        when:
        def result = quizManagementService.startQuiz(command)

        then:
        1 * quizRepository.getById(id, filter.visibilities(), filter.statuses()) >> Optional.of(new Quiz(id,"someTitle",
                "desc","tester1",null,1L,
                [new Question("id1", "qId1", "Some question", "expl", "tags", "tester1",
                        Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                        Question.Status.PUBLISHED, Question.Visibility.PUBLIC,
                        [new Answer(1, "some answer", false),
                         new Answer(2, "another answer", true)], 1L),
                 new Question("id2", "qId2", "Some question", "expl", "tags", "tester1",
                         Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                         Question.Status.PUBLISHED, Question.Visibility.PUBLIC,
                         [new Answer(1, "answer", false),
                          new Answer(2, "different answer", true)], 1L)
                ],
                Quiz.Type.EXAM, Quiz.Status.PUBLISHED, Quiz.Visibility.PUBLIC,
                70, true, null, null, null))

        result.id == id
        result.title == "someTitle"
        result.sessionId != null
        result.questions.size() == 2
        result.questions[0].questionId() == "qId1"
    }


    def "should create quiz"() {
        given:
        def command = new CreateQuizCommand("New Quiz", "user02", List.of("qId1", "qId2"))
        def quizId = "deadbeefcafebabe12345602"

        when:
        def result = quizManagementService.createQuiz(command)

        then:
        1 * quizRepository.createQuiz(command) >> quizId
        result == quizId
    }

    def "should add questions to quiz"() {
        given:
        def quizId = "deadbeefcafebabe12345603"
        def command = new CreateQuizCommand("New Quiz", "user02", List.of())
        var filter = QuestionFilter.assignable();

        when:
        quizManagementService.createQuiz(command)
        quizManagementService.assignQuestionsToQuiz(quizId, Set.of("qId1", "qId2"))

        then:
        1 * quizRepository.createQuiz(command) >> quizId
        1 * quizRepository.addQuestionsToQuiz(quizId, Set.of("qId1", "qId2"), filter.statuses(), filter.visibilities())
    }

    def "should remove questions from quiz"() {
        given:
        def quizId = "deadbeefcafebabe12345604"
        def command = new CreateQuizCommand("New Quiz", "user02", List.of())

        when:
        quizManagementService.createQuiz(command)
        quizManagementService.removeQuestionsFromQuiz(quizId, Set.of("qId1", "qId2"))

        then:
        1 * quizRepository.createQuiz(command) >> quizId
        1 * quizRepository.removeQuestionsFromQuiz(quizId, Set.of("qId1", "qId2"))
    }

}