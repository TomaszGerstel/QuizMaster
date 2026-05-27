package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {
    Optional<QuestionDTO> findById(String id);
    List<QuestionDTO> getAll();
    List<QuestionDTO> getForTag(String tag);
    void createQuestion(CreateQuestionCommand command, String id);
}
