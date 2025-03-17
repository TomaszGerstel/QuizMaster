import { QuestionSolution } from "./question-solution.model";

export interface QuizSubmissionRequest {
  quizId: string;
  sessionId: string;
  solutions: QuestionSolution[];
}
