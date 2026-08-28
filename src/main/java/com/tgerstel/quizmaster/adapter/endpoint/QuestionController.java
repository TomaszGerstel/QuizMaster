package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.model.QuestionMode;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuizManager quizService;

    public QuestionController(QuizManager quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getQuestions(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) QuestionMode mode,
            @RequestParam(required = false) String forQuizId
    ) {
        return ResponseEntity.ok(quizService.getQuestions(tag, mode, forQuizId));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createQuestion(@RequestBody final CreateQuestionCommand request) {
        var savedId = quizService.createQuestion(request);
        return ResponseEntity.ok(Map.of("questionId", savedId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateQuestion(@PathVariable String id,
                                                 @RequestBody final CreateQuestionCommand request
    ) {
        quizService.updateQuestion(id, request);
        return ResponseEntity.ok(Map.of("status:", "Question %s updated".formatted(id)));
    }

}
