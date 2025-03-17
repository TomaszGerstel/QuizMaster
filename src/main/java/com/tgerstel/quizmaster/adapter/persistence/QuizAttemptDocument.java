package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String quizId;
    private Instant startTime;
    private Instant endTime;

    public QuizAttemptDTO toDTO() {
        return new QuizAttemptDTO(sessionId, quizId, startTime, endTime);
    }

    public static QuizAttemptDocument fromDTO(QuizAttemptDTO dto) {
        return new QuizAttemptDocument(dto.getSessionId(), dto.getQuizId(), dto.getStartTime(), dto.getEndTime());
    }
}
