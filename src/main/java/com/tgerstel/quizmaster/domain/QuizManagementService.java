package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuizManagementService implements QuizManager {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;

    public QuizManagementService(QuizRepository quizRepository, QuizAttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
    }

    public List<QuizBasicDTO> getAllQuizzes() {
        return quizRepository.getAll();
    }

    public QuizDTO getQuiz(String id) {
        var quiz = quizRepository.getById(id).orElseThrow(() -> new QuizNotFoundException(id));
        var sessionId = UUID.randomUUID().toString();
        attemptRepository.create(QuizAttemptDTO.startAttempt(sessionId, id));
        quiz.setSessionId(sessionId);
        return quiz;
    }

}
