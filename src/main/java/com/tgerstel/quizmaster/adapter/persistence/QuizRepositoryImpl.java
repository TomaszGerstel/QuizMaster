package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class QuizRepositoryImpl implements QuizRepository {

    private final MongoQuizRepository mongoQuizRepository;

    public List<QuizBasicDTO> getAll() {
        var found = mongoQuizRepository.findAll();
        return found.stream().map(QuizDocument::toBasicDTO).collect(Collectors.toList());
    }

    public Optional<QuizDTO> getById(final String id) {
        var found = mongoQuizRepository.findById(id);
        return found.map(QuizDocument::toDTO);
    }

    @Override
    public Optional<QuizEvalDTO> getEvalById(String id) {
        var found = mongoQuizRepository.findById(id);
        return found.map(QuizDocument::toEvalDTO);
    }

}
