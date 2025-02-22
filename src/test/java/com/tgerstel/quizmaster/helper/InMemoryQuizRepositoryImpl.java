package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.adapter.persistence.QuizDocument;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("test")
@Primary
public class InMemoryQuizRepositoryImpl implements QuizRepository {

    private final List<QuizDocument> quizDocuments = new ArrayList<>();

    @Override
    public List<QuizBasicDTO> getAll() {
        return quizDocuments.stream()
                .map(QuizDocument::toBasicDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<QuizDTO> getById(String id) {
        return quizDocuments.stream()
                .filter(quiz -> quiz.getId().equals(id))
                .findFirst()
                .map(QuizDocument::toDTO);
    }

    @Override
    public Optional<QuizEvalDTO> getEvalById(String id) {
        return quizDocuments.stream()
                .filter(quiz -> quiz.getId().equals(id))
                .findFirst()
                .map(QuizDocument::toEvalDTO);
    }

    public void save(QuizDocument quizDocument) {
        quizDocuments.add(quizDocument);
    }

    public void clear() {
        quizDocuments.clear();
    }
}

