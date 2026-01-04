package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizManager quizService;

    public QuizController(QuizManager quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<QuizBasicDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @PostMapping("/start")
    public ResponseEntity<QuizDTO> startQuiz(@RequestBody final StartQuizRequest request) {
        return ResponseEntity.ok(quizService.startQuiz(request.toCommand()));
    }

}
