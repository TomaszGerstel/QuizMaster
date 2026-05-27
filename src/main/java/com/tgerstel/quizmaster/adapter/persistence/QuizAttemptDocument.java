package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "quiz_attempt")
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDocument {

    @Id
    private String sessionId;
    private ObjectId quizId;
    private String userName;
    private String userEmail;
    private Instant startTime;
    private Instant endTime;
    private int questionCount;
    private int correctAnswers;

    public QuizAttemptDTO toDTO() {
        return new QuizAttemptDTO(sessionId, quizId.toString(), userName, userEmail, startTime, endTime,
                questionCount, correctAnswers);
    }

    public static QuizAttemptDocument initAttempt(QuizAttemptDTO dto) {
        return new QuizAttemptDocument(dto.getSessionId(), new ObjectId(dto.getQuizId()), dto.getUserName(),
                dto.getUserEmail(), dto.getStartTime(), null, dto.getQuestionsCount(), 0);
    }

    public void completeQuizAttempt(int score, Instant endTime) {
        this.correctAnswers = score;
        this.endTime = endTime;
    }
}
