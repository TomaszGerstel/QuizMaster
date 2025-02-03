package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.model.QuizResult;
import com.tgerstel.quizmaster.domain.port.QuizEvaluator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/submission")
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
