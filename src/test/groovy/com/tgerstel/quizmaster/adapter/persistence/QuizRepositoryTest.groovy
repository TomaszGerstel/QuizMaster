package com.tgerstel.quizmaster.adapter.persistence

import spock.lang.Specification

class QuizRepositoryTest extends Specification {

    private MongoQuizRepository mongoRepository = Mock()
    private QuizRepositoryImpl repository = new QuizRepositoryImpl(mongoRepository)

    def "should return all quizzes info"() {
        given:
        def q1Id = "q2025012"
        def quizDocument1 = createDefaultQuizDocument("Test Quiz 1", q1Id)
        def quizDocument2 = createDefaultQuizDocument()

        when:
        def quizzes = repository.getAll()

        then:
        1 * mongoRepository.findAll() >> [quizDocument1, quizDocument2]
        quizzes.size() == 2
        quizzes[0].title == "Test Quiz 1"
        quizzes[0].questionsQuantity == 2
        quizzes[0].id == q1Id
    }

    def "should get quiz DTO by id"() {
        given:
        def id = "q2025015"
        def quizDocument = createDefaultQuizDocument("Test Quiz 2", id)
        mongoRepository.findById(id) >> Optional.of(quizDocument)

        when:
        def quiz = repository.getById(id).get()

        then:
        1 * mongoRepository.findById(id) >> Optional.of(quizDocument)
        quiz.id == id
        quiz.title == "Test Quiz 2"
        quiz.questions.size() == 2
        quiz.questions*.question.containsAll(["Capital of England", "2 + 2"])
        quiz.questions.find { it.question == "Capital of England" }.answers*.content.containsAll(["London", "Warsaw"])
        quiz.questions.find { it.question == "2 + 2" }.answers*.content.containsAll(["4", "5", "0"])
    }

    def "should return quiz for eval by id"() {
        given:
        def id = "q2025066"
        def quizDocument = createDefaultQuizDocument("Test Quiz", id)
        mongoRepository.findById(id) >> Optional.of(quizDocument)

        when:
        def quiz = repository.getEvalById(id).get()

        then:
        1 * mongoRepository.findById(id) >> Optional.of(quizDocument)
        quiz.id() == id
        quiz.questions().size() == 2
        !quiz.questions()[0].id().isEmpty()
        quiz.questions()[0].answers()[0].no() == 1
        quiz.questions()[0].answers()[0].correct
        quiz.questions()[0].answers()[1].no() == 2
        !quiz.questions()[0].answers()[1].correct

    }

    private static QuizDocument createDefaultQuizDocument(String title = "Default Title", String id = "Default ID") {
        return createQuizDocument(title, id,
                createQuestion("Capital of England",
                        createAnswer("London", true, 1),
                        createAnswer("Warsaw", false, 2)),
                createQuestion("2 + 2",
                        createAnswer("4", true, 1),
                        createAnswer("5", false, 2),
                        createAnswer("0", false, 3)))
    }

    private static QuizDocument createQuizDocument(String title, String id, QuestionDocument... questions) {
        def quizDocument = new QuizDocument()
        quizDocument.title = title
        quizDocument.id = id
        quizDocument.questions = questions.toList()
        return quizDocument
    }

    private static QuestionDocument createQuestion(String question, BaseAnswer... answers) {
        def questionDocument = new QuestionDocument()
        questionDocument.id = new Random().nextInt().toString()
        questionDocument.question = question
        questionDocument.answers = answers.toList()
        return questionDocument
    }

    private static BaseAnswer createAnswer(String value, boolean correct, Integer no) {
        def baseAnswer = new BaseAnswer()
        baseAnswer.no = no
        baseAnswer.value = value
        baseAnswer.correct = correct
        return baseAnswer
    }


}
