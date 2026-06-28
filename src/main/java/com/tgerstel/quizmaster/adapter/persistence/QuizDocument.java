package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

@Data
@Document(collection = "quiz")
@NoArgsConstructor
@AllArgsConstructor
public class QuizDocument {
    @Id
    private ObjectId id;

    private String title;
    private String description;

    private String author;
    private String ownerId;

    private Quiz.Type type;
    private Quiz.Status status;
    private Quiz.Visibility visibility;

    private Integer passRate;
    private boolean shuffleQuestions;

    Set<String> questionIds;

    @Version
    private Long version;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
    private Instant publishedAt;

    public QuizBasicDTO toBasicDTO() {
        var count = !(questionIds == null) ? questionIds.size() : 0;
        return new QuizBasicDTO(id.toString(), title, count);
    }

    public Quiz toDomain(Set<QuestionDocument> questions) {
        var mappedQuestions = questions.stream().map(QuestionDocument::toDomain).toList();

        var mutableQuestions = new ArrayList<>(mappedQuestions);
        if (shuffleQuestions) Collections.shuffle(mutableQuestions);

        return new Quiz(
                id.toString(),
                title,
                description,
                author,
                ownerId,
                version,
                mutableQuestions,
                type,
                status,
                visibility,
                passRate,
                shuffleQuestions,
                createdAt,
                updatedAt,
                publishedAt
                );
    }

    public QuizDTO toDetailedDTO(Set<QuestionDocument> questions) {
        var mappedQuestions = questions.stream().map(QuestionDocument::toDTO).toList();
        return new QuizDTO(id.toString(), title, mappedQuestions);
    }

}
