package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Profile("test")
@Primary
public class InMemoryQuestionRepository implements QuestionRepository {

    @Override
    public List<QuestionDTO> getAllForIds(Set<String> ids) {
        return List.of();
    }

    @Override
    public Optional<QuestionDTO> findByIdStatusAndVisibility(String id,
                                                             EnumSet<Question.Status> status,
                                                             EnumSet<Question.Visibility> visibility) {
        return Optional.empty();
    }

    @Override
    public List<QuestionDTO> getAllInLatestVersions(
            EnumSet<Question.Status> status,
            EnumSet<Question.Visibility> visibility
    ) {
        return List.of();
    }

    @Override
    public List<QuestionDTO> getForTagInLatestVersions(
            String tag,
            EnumSet<Question.Status> status,
            EnumSet<Question.Visibility> visibility
    ) {
        return List.of();
    }

    @Override
    public String createQuestion(CreateQuestionCommand command,  String questionId, Long version) {
        return "mocked-question-id";
    }

//    @Override
//    public String updateQuestion(CreateQuestionCommand command, String id) {
//        return "";
//    }
}
