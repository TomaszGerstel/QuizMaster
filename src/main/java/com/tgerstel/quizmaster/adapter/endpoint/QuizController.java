package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<QuizToSolveDTO> startQuiz(@RequestBody final StartQuizRequest request) {
        return ResponseEntity.ok(quizService.startQuiz(request.toCommand()));
    }

    @PostMapping("/manage/new")
    public ResponseEntity<Map<String, String>> createQuiz(@RequestBody final CreateQuizCommand request) {
        Map<String, String> response = new HashMap<>();
        var quizId = quizService.createQuiz(request);
        response.put("quizId", quizId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/manage")
    public ResponseEntity<List<QuizDTO>> getAllQuizzesForManagement() {
        return ResponseEntity.ok(quizService.getAllEditableQuizzesDetailed());
    }

    @PostMapping("/assign-questions")
    public ResponseEntity<?> assignQuestionsToQuiz(@RequestBody final AssignQuestionsRequest request) {
        quizService.assignQuestionsToQuiz(request.quizId(), request.questionIds());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeQuiz(@PathVariable String id) {
        // This is a placeholder implementation.
        return ResponseEntity.ok("Quiz removed");
    }

    @PostMapping("/remove-questions")
    public ResponseEntity<?> removeQuestionsFromQuiz(@RequestBody final RemoveQuestionsRequest request) {
        quizService.removeQuestionsFromQuiz(request.quizId(), request.questionIds());
        return ResponseEntity.ok(
                Map.of("message", "Questions removed from quiz")
        );
    }
}
