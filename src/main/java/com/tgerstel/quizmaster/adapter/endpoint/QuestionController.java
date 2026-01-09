package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuizManager quizService;

    public QuestionController(QuizManager quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(quizService.getAllQuestions());
    }

    @GetMapping("/{tag}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByTag(@PathVariable String tag) {
        return ResponseEntity.ok(quizService.getQuestionsForTag(tag));
    }

    @PostMapping
    public ResponseEntity<String> createQuestion(@RequestBody final CreateQuestionCommand request) {
        return ResponseEntity.ok(quizService.createQuestion(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateQuestion(@PathVariable String id, @RequestBody final UpdateQuestionRequest request) {
        // This is a placeholder implementation.
        return ResponseEntity.ok("Question updated");
    }


}
