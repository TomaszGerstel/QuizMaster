package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {
    Optional<QuestionDTO> findById(ObjectId id);
    List<QuestionDTO> getAll();
    List<QuestionDTO> getForTag(String tag);
    void createQuestion(CreateQuestionCommand command, String id);
}
