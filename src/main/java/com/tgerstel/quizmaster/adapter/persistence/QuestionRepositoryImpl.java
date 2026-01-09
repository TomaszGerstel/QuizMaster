package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final MongoQuestionRepository mongoQuestionRepository;


    @Override
    public Optional<QuestionDTO> findById(ObjectId id) {
        return mongoQuestionRepository.findById(id).map(QuestionDocument::toDTO);
    }

    @Override
    public List<QuestionDTO> getAll() {
        return mongoQuestionRepository.findAll().stream()
                .map(QuestionDocument::toDTO)
                .toList();
    }

    // not used currently
    public List<QuestionDTO> getAllForIds(List<ObjectId> ids) {
        return mongoQuestionRepository.findAllById(ids).stream()
                .map(QuestionDocument::toDTO)
                .toList();
    }

    @Override
    public List<QuestionDTO> getForTag(String tag) {
        return mongoQuestionRepository.findByTagsContaining(tag).stream()
                .map(QuestionDocument::toDTO)
                .toList();
    }

    @Override
    public void createQuestion(CreateQuestionCommand command, String id) {
        var questionDocument = QuestionDocument.create(command, id);
        mongoQuestionRepository.save(questionDocument);
    }
}
