import { QuestionSolution } from "./question-solution.model";

export interface QuizSubmissionRequest {
  quizId: string;
  solutions: QuestionSolution[];
}
