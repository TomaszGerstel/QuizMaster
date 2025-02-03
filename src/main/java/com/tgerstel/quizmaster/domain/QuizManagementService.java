package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizManagementService implements QuizManager {

    private final QuizRepository quizRepository;

    public QuizManagementService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public List<QuizBasicDTO> getAllQuizzes() {
        return quizRepository.getAll();
    }

    public QuizDTO getQuiz(String id) {
        return quizRepository.getById(id).orElseThrow(() -> new QuizNotFoundException(id));
    }

}
