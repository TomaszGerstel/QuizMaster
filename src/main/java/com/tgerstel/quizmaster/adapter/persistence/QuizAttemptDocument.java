package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.AttemptToEvalDTO;
import com.tgerstel.quizmaster.domain.model.QuizAttempt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "quiz_attempt")
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDocument {

    @Id
    private String sessionId;
    private ObjectId quizId;
    private String quizName;
    private String quizDescription;
    private Long quizVersion;
    private Integer passRate;
    private String userName;
    private String userEmail;
    private boolean sendEmail;
    private Instant startTime;
    private Instant endTime;
    private int questionCount;
    private int correctAnswers;

    @DBRef
    private Set<QuestionDocument> questions;

    public QuizAttempt toDomain() {
        return new QuizAttempt(
                sessionId,
                quizId.toString(),
                quizName,
                quizDescription,
                quizVersion,
                passRate,
                userName,
                userEmail,
                sendEmail,
                startTime,
                endTime,
                questionCount,
                correctAnswers,
                questions.stream().map(QuestionDocument::toDomain).toList()
        );
    }

    public AttemptToEvalDTO toEvalDTO() {
        return new AttemptToEvalDTO(
                sessionId,
                quizId.toString(),
                userEmail,
                startTime,
                questions.stream().map(QuestionDocument::toEvalQuestion).toList());
    }

    public static QuizAttemptDocument initAttempt(QuizAttempt dto, Set<QuestionDocument> questions) {
        return new QuizAttemptDocument(
                dto.getSessionId(),
                new ObjectId(dto.getQuizId()),
                dto.getQuizName(),
                dto.getQuizDescription(),
                dto.getQuizVersion(),
                dto.getPassRate(),
                dto.getUserName(),
                dto.getUserEmail(),
                dto.isSendEmail(),
                dto.getStartTime(),
                null,
                dto.getQuestionsCount(),
                0,
                questions
        );
    }

    public void completeQuizAttempt(int score, Instant endTime) {
        this.correctAnswers = score;
        this.endTime = endTime;
    }
}
