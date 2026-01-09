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
    private String question;
    private String tags;
    private String explanation;
    private String author;
    private List<BaseAnswer> answers;

    public QuestionDocument(String question, String tags, String explanation, String author, List<BaseAnswer> answers) {
        this.question = question;
        this.tags = tags;
        this.explanation = explanation;
        this.author = author;
        this.answers = answers;
    }

    public Question toQuestion() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toAnswer).toList();
        var shuffledAnswers = new ArrayList<>(mappedAnswers);
        Collections.shuffle(shuffledAnswers);
        return new Question(id.toString(), question, shuffledAnswers);
    }

    public EvalQuestion toEvalQuestion() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toEvalAnswer).toList();
        return new EvalQuestion(id.toString(), explanation, mappedAnswers, author);
    }

    public QuestionDTO toDTO() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toAnswerDTO).toList();
        return new QuestionDTO(id.toString(), question, tags, explanation, author, mappedAnswers);
    }

    public static QuestionDocument create(CreateQuestionCommand command, String id) {
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
                answerDocs
        );

    }
}