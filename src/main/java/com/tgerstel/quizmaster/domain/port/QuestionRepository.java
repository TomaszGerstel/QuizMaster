package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.model.Question;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionRepository {
    List<QuestionDTO> getAllForIds(Set<String> ids);
    Optional<QuestionDTO> findByIdStatusAndVisibility(String id,
                                                      EnumSet<Question.Status> status,
                                                      EnumSet<Question.Visibility> visibility);
    List<QuestionDTO> getAllInLatestVersions(EnumSet<Question.Status> status,
                                             EnumSet<Question.Visibility> visibility);
    List<QuestionDTO> getForTagInLatestVersions(String tag, EnumSet<Question.Status> status,
                                                EnumSet<Question.Visibility> visibility);
    String createQuestion(CreateQuestionCommand command, String questionId, Long version);
//    String updateQuestion(CreateQuestionCommand command, String questionId);
}
