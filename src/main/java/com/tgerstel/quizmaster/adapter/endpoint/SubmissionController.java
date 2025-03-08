package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.model.QuizResult;
import com.tgerstel.quizmaster.domain.port.QuizEvaluator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("api/submission")
public class SubmissionController {

    private final QuizEvaluator quizEvaluator;

    public SubmissionController(QuizEvaluator quizEvaluator) {
        this.quizEvaluator = quizEvaluator;
    }

    @PostMapping
    public ResponseEntity<QuizResult> submitQuiz(@RequestBody final QuizSubmissionRequest request) {
        return ResponseEntity.accepted().body(quizEvaluator.submitQuiz(request.toCommand()));
    }

}
