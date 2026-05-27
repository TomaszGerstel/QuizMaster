package com.tgerstel.quizmaster.adapter.persistence

import org.bson.types.ObjectId
import spock.lang.Specification

class QuizRepositoryTest extends Specification {

    private MongoQuizRepository mongoQuizRepository = Mock()
    private MongoQuestionRepository mongoQuestionRepository = Mock()

    private QuizRepositoryImpl repository = new QuizRepositoryImpl(mongoQuizRepository, mongoQuestionRepository)

    def "should return all quizzes info"() {
        given:
        def objId = new ObjectId("ef77bcf86cd7990000000111")
        def quizDocument1 = createDefaultQuizDocument("Test Quiz 1", objId)
        def quizDocument2 = createDefaultQuizDocument()

        when:
        def quizzes = repository.getAll()

        then:
        1 * mongoQuizRepository.findAll() >> [quizDocument1, quizDocument2]
        quizzes.size() == 2
        quizzes[0].title == "Test Quiz 1"
        quizzes[0].questionsQuantity == 2
        quizzes[0].id == objId.toString()
    }

    def "should get quiz DTO by id"() {
        given:
        def id = "ef77bcf86cd7990000000222"
        def objId = new ObjectId(id)
        def quizDocument = createDefaultQuizDocument("Test Quiz 2", objId)
        mongoQuizRepository.findById(objId) >> Optional.of(quizDocument)

        when:
        def quiz = repository.getById(id).get()

        then:
        1 * mongoQuizRepository.findById(objId) >> Optional.of(quizDocument)
        quiz.id == id
        quiz.title == "Test Quiz 2"
        quiz.questions.size() == 2
        quiz.questions*.question.containsAll(["Capital of England", "2 + 2"])
        quiz.questions.find { it.question == "Capital of England" }.answers*.content.containsAll(["London", "Warsaw"])
        quiz.questions.find { it.question == "2 + 2" }.answers*.content.containsAll(["4", "5", "0"])
    }

    def "should return quiz for eval by id"() {
        given:
        def id = "ef77bcf86cd7990000000333"
        def objId = new ObjectId(id)
        def quizDocument = createDefaultQuizDocument("Test Quiz", objId)
        mongoQuizRepository.findById(objId) >> Optional.of(quizDocument)

        when:
        def quiz = repository.getEvalById(id).get()

        then:
        1 * mongoQuizRepository.findById(objId) >> Optional.of(quizDocument)
        quiz.id() == id.toString()
        quiz.questions().size() == 2
        !quiz.questions()[0].id().toString().isEmpty()
        quiz.questions()[0].answers()[0].no() == 1
        quiz.questions()[0].answers()[0].correct
        quiz.questions()[0].answers()[1].no() == 2
        !quiz.questions()[0].answers()[1].correct
    }

    def "should add questions to quiz"() {
        given:
        def quizId = "ef77bcf86cd7990000000444"
        def questionId = "ef77bcf86cd7990000000555"
        def objQuizId = new ObjectId(quizId)
        def objQuestionId = new ObjectId(questionId)

        def quizDocument = createDefaultQuizDocument("Test Quiz", objQuizId)
        mongoQuizRepository.findById(objQuizId) >> Optional.of(quizDocument)
        mongoQuestionRepository.findById(objQuestionId)
                >> Optional.of(createQuestion("Sample Question", createAnswer("Answer 1", true, 1)))

        when:
        repository.addQuestionsToQuiz(quizId, List.of(questionId))

        then:
        1 * mongoQuizRepository.findById(objQuizId) >> Optional.of(quizDocument)
        1 * mongoQuestionRepository.findById(objQuestionId)
                >> Optional.of(createQuestion("Sample Question", createAnswer("Answer 1", true, 1)))

    }

    def "should remove questions from quiz"() {
        given:
        def quizId = "ef77bcf86cd7990000000444"
        def questionId = "ef77bcf86cd7990000000555"
        def objQuizId = new ObjectId(quizId)
        def objQuestionId = new ObjectId(questionId)

        def questionDocument = createQuestion("Sample Question", createAnswer("Answer 1", true, 1))
        questionDocument.id = objQuestionId

        def quizDocument = createDefaultQuizDocument("Test Quiz", objQuizId)
        quizDocument.questions.add(questionDocument)

        mongoQuizRepository.findById(objQuizId) >> Optional.of(quizDocument)

        when:
        repository.removeQuestionsFromQuiz(quizId, List.of(questionId))

        then:
        1 * mongoQuizRepository.findById(objQuizId) >> Optional.of(quizDocument)
        1 * mongoQuizRepository.save(_) >> { QuizDocument savedQuiz ->
            assert savedQuiz.questions.size() == 2 // Original 2 questions + 1 added - 1 removed
            assert savedQuiz.questions.find { it.id == objQuestionId } == null // Ensure the question was removed
        }
        1 * mongoQuestionRepository.findById(objQuestionId) >> Optional.of(questionDocument)

    }

//    def "should delete quiz by id"() {
//        given:
//        def quizId = "ef77bcf86cd7990000000666"
//        def objQuizId = new ObjectId(quizId)
//
//        when:
//        repository.deleteById(quizId)
//
//        then:
//        1 * mongoQuizRepository.deleteById(objQuizId)
//    }


    private static QuizDocument createDefaultQuizDocument(String title = "Default Title", ObjectId id = new ObjectId()) {
        return createQuizDocument(title, id,
                createQuestion("Capital of England",
                        createAnswer("London", true, 1),
                        createAnswer("Warsaw", false, 2)),
                createQuestion("2 + 2",
                        createAnswer("4", true, 1),
                        createAnswer("5", false, 2),
                        createAnswer("0", false, 3)))
    }

    private static QuizDocument createQuizDocument(String title, ObjectId id, QuestionDocument... questions) {
        def quizDocument = new QuizDocument()
        quizDocument.title = title
        quizDocument.id = id
        quizDocument.questions = questions.toList()
        return quizDocument
    }

    private static QuestionDocument createQuestion(String question, BaseAnswer... answers) {
        def questionDocument = new QuestionDocument()
        questionDocument.id = new ObjectId()
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
