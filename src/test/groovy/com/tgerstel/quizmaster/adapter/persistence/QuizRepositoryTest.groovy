package com.tgerstel.quizmaster.adapter.persistence

import com.tgerstel.quizmaster.domain.model.Quiz
import com.tgerstel.quizmaster.domain.model.Question
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
        def visibility = EnumSet.of(Quiz.Visibility.PUBLIC)
        def status = EnumSet.of(Quiz.Status.PUBLISHED)

        when:
        def quizzes = repository.getAllQuizzesForVisibilityAndStatus(visibility, status)

        then:
        1 * mongoQuizRepository.findAllByVisibilityInAndStatusIn(visibility, status) >> [quizDocument1, quizDocument2]
        quizzes.size() == 2
        quizzes[0].title == "Test Quiz 1"
        quizzes[0].questionsQuantity == 3
        quizzes[0].id == objId.toString()
    }

    def "should get quiz DTO by id"() {
        given:
        def id = "ef77bcf86cd7990000000222"
        def objId = new ObjectId(id)
        def questionIds = Set.of("question0011", "question0012")
        def quizDocument = createDefaultQuizDocument("Test Quiz 2", objId, questionIds)
        mongoQuizRepository.findById(objId) >> Optional.of(quizDocument)
        def visibility = EnumSet.of(Quiz.Visibility.PUBLIC)
        def status = EnumSet.of(Quiz.Status.PUBLISHED)

        when:
        def quiz = repository.getById(id, visibility, status).get()

        then:
        1 * mongoQuizRepository.findById(objId) >> Optional.of(quizDocument)
        1 * mongoQuestionRepository.findByQuestionIdIn(questionIds) >> Set.of(
                createQuestion("Sample Question 11", "question0011",
                        createAnswer("Answer 111", true, 1),
                        createAnswer("Answer 112", true, 1)
                ),
                createQuestion("Sample Question 12", "question0012",
                        createAnswer("Answer 121", true, 1),
                        createAnswer("Answer 122", true, 1)
                )
        )
        quiz.id == id
        quiz.title == "Test Quiz 2"
        quiz.questions.size() == 2
        quiz.questions*.question.containsAll(["Sample Question 11", "Sample Question 12"])
        quiz.questions.find { it.question == "Sample Question 11" }.answers*.content.containsAll(["Answer 111", "Answer 112"])
        quiz.questions.find { it.question == "Sample Question 12" }.answers*.content.containsAll(["Answer 121", "Answer 122"])
    }

    def "should add questions to quiz"() {
        given:
        def quizId = "ef77bcf86cd7990000000444"
        def questionIds = Set.of("ef77bcf86cd7990000000555")
        def objQuizId = new ObjectId(quizId)
        def visibility = EnumSet.of(Question.Visibility.PUBLIC)
        def status = EnumSet.of(Question.Status.PUBLISHED)

        def quizDocument = createDefaultQuizDocument("Test Quiz", objQuizId, new HashSet<String>())

        when:
        repository.addQuestionsToQuiz(quizId, questionIds, status, visibility)

        then:
        1 * mongoQuizRepository.findById(objQuizId) >> Optional.of(quizDocument)
        1 * mongoQuestionRepository.findByQuestionIdIn(questionIds)
                >> Set.of(createQuestion("Sample Question", "ef77bcf86cd7990000000555", createAnswer("Answer 1", true, 1)))
    }

    def "should remove questions from quiz"() {
        given:
        def quizId = "ef77bcf86cd7990000000444"
        def questionToRemoveId = "ef77bcf86cd7990000000555"
        def objQuizId = new ObjectId(quizId)
        def questionIds = new HashSet<String>()
        questionIds.add("another0003")
        questionIds.add("moreQuestions004")

        questionIds.add(questionToRemoveId)

        def quizDocument = createDefaultQuizDocument("Test Quiz", objQuizId, questionIds)

        when:
        repository.removeQuestionsFromQuiz(quizId, Set.of(questionToRemoveId))

        then:
        1 * mongoQuizRepository.findById(objQuizId) >> Optional.of(quizDocument)
        1 * mongoQuizRepository.save(_) >> { QuizDocument savedQuiz ->
            assert savedQuiz.questionIds.size() == 2 // Original 2 questions + 1 added - 1 removed
            assert savedQuiz.questionIds.find { it == questionToRemoveId } == null // Ensure the question was removed
        }

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


    private static QuizDocument createDefaultQuizDocument(
            String title = "Default Title",
            ObjectId id = new ObjectId(),
            Set<String> qIds = Set.of("question001", "question002", "question003")
    ) {
        return createQuizDocument(title, id, qIds)
    }

    private static QuizDocument createQuizDocument(String title, ObjectId id, Set<String> qIds) {
        def quizDocument = new QuizDocument()
        quizDocument.title = title
        quizDocument.id = id
        quizDocument.questionIds = qIds
        return quizDocument
    }

    private static QuestionDocument createQuestion(String question, String questionId, BaseAnswer... answers) {
        def questionDocument = new QuestionDocument()
        questionDocument.id = new ObjectId()
        questionDocument.questionId = questionId
        questionDocument.question = question
        questionDocument.answers = answers.toList()
        questionDocument.status = Question.Status.PUBLISHED
        questionDocument.visibility = Question.Visibility.PUBLIC
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
