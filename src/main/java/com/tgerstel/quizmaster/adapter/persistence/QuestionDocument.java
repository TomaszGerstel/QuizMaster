package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.Question;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Document(collection = "question")
@NoArgsConstructor
public class QuestionDocument {
    private ObjectId id;
    private String questionId;
    private String question;
    private String tags;
    private String explanation;
    private String author;
    private String ownerId;
    private List<BaseAnswer> answers;

    private Question.Type  type;
    private Question.ScoringStrategyType scoringStrategyType;
    private Question.Visibility visibility;
    private Question.Status status;

    private Long version;

    public QuestionDocument(String question, String tags, String explanation, String author,
                            List<BaseAnswer> answers, Long version) {
        this.question = question;
        this.tags = tags;
        this.explanation = explanation;
        this.author = author;
        this.answers = answers;
        this.version = version;

        // temp
        this.type = Question.Type.MULTIPLE_CHOICE;
        this.scoringStrategyType = Question.ScoringStrategyType.ALL_OR_NOTHING;
        this.visibility = Question.Visibility.PUBLIC;
        this.status = Question.Status.PUBLISHED;
    }

    public Question toDomain() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toAnswer).toList();
        var shuffledAnswers = new ArrayList<>(mappedAnswers);
        Collections.shuffle(shuffledAnswers);
        return new Question(
                id.toString(),
                questionId,
                question,
                explanation,
                tags,
                author,
                type,
                scoringStrategyType,
                status,
                visibility,
                shuffledAnswers,
                version
        );
    }

    public EvalQuestion toEvalQuestion() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toEvalAnswer).toList();
        return new EvalQuestion(questionId, type, scoringStrategyType, explanation, mappedAnswers);
    }

    public QuestionDTO toDTO() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toAnswerDTO).toList();
        return new QuestionDTO(questionId, question, tags, explanation, author, version, mappedAnswers);
    }

    public static QuestionDocument create(CreateQuestionCommand command, Long version) {
        var answerDocs = command.answers().stream().map(ans -> {
            var baseAnswer = new BaseAnswer();
            baseAnswer.setNo(command.answers().indexOf(ans) + 1);
            baseAnswer.setValue(ans.value());
            baseAnswer.setCorrect(ans.isCorrect());
            return baseAnswer;
        }).toList();

        return new QuestionDocument(
                command.question(),
                command.tags(),
                command.explanation(),
                command.author(),
                answerDocs,
                version
        );
    }

    public QuestionDocument update(CreateQuestionCommand command) {
        this.question = command.question();
        this.tags = command.tags();
        this.explanation = command.explanation();
        this.author = command.author();
        this.answers = command.answers().stream().map(ans -> {
            var baseAnswer = new BaseAnswer();
            baseAnswer.setNo(command.answers().indexOf(ans) + 1);
            baseAnswer.setValue(ans.value());
            baseAnswer.setCorrect(ans.isCorrect());
            return baseAnswer;
        }).toList();
        return this;
    }

}