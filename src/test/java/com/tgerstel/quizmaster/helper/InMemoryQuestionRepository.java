package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("test")
@Primary
public class InMemoryQuestionRepository implements QuestionRepository {
    @Override
    public Optional<QuestionDTO> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<QuestionDTO> getAll() {
        return List.of();
    }

    @Override
    public List<QuestionDTO> getForTag(String tag) {
        return List.of();
    }

    @Override
    public void createQuestion(CreateQuestionCommand command, String id) {

    }
}
