package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Profile({"prod", "dev", "dokploy"})
public class QuestionRepositoryImpl implements QuestionRepository {

    private final MongoQuestionRepository mongoQuestionRepository;


    @Override
    public Optional<QuestionDTO> findByIdStatusAndVisibility(
            String id,
            EnumSet<Question.Status> status,
            EnumSet<Question.Visibility> visibility) {
        return mongoQuestionRepository.findTopByQuestionIdOrderByVersionDesc(id)
                .filter(q -> status.contains(q.getStatus()) && visibility.contains(q.getVisibility()))
                .map(QuestionDocument::toDTO);
    }

    @Override
    public List<QuestionDTO> getAllInLatestVersions(
            EnumSet<Question.Status> status,
            EnumSet<Question.Visibility> visibility
    ) {
        return mongoQuestionRepository.findAll().stream()
                .collect(Collectors.groupingBy(QuestionDocument::getQuestionId))
                .values().stream()
                .map(list -> list.stream()
                .max(Comparator.comparing(QuestionDocument::getVersion))
                .orElseThrow())
                .filter(q ->
                        status.contains(q.getStatus()) &&  visibility.contains(q.getVisibility()))
                .map(QuestionDocument::toDTO)
                .toList();
    }

    @Override
    public List<QuestionDTO> getAllForIds(Set<String> ids) {
        return mongoQuestionRepository.findByQuestionIdIn(ids).stream()
                .map(QuestionDocument::toDTO).toList();
    }

    @Override
    public List<QuestionDTO> getForTagInLatestVersions(
            String tag,
            EnumSet<Question.Status> status,
            EnumSet<Question.Visibility> visibility
    ) {
        return mongoQuestionRepository.findByTagsContainingIgnoreCase(tag)
                .stream().collect(Collectors.groupingBy(QuestionDocument::getQuestionId))
                .values().stream()
                .map(list -> list.stream()
                .max(Comparator.comparing(QuestionDocument::getVersion))
                .orElseThrow())
                .filter(q ->
                        status.contains(q.getStatus()) &&  visibility.contains(q.getVisibility()))
                .map(QuestionDocument::toDTO).toList();
    }

    @Override
    public String createQuestion(CreateQuestionCommand command, String questionId, Long version) {
        var questionDocument = QuestionDocument.create(command, version);
        questionDocument.setQuestionId(questionId);
        return mongoQuestionRepository.save(questionDocument).getId().toString();
    }

//    @Override
//    public String updateQuestion(CreateQuestionCommand command, String questionId) {
//        return mongoQuestionRepository.findLatestVersionByQuestionId(questionId).map(doc -> {
//            doc.update(command);
//            return mongoQuestionRepository.save(doc).getId().toString();
//        }).orElseThrow(() -> new RuntimeException("Question not found"));
//    }
}
