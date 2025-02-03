package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    List<QuizBasicDTO> getAll();
    Optional<QuizDTO> getById(String id);
    Optional<QuizEvalDTO> getEvalById(String id);
}
