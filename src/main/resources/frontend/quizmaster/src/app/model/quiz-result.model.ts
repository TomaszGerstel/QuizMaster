export interface QuizResult {
  quizId: string;
  isPositive: boolean;
  quizScore: number;
  questionsCount: number;
  answersReport: { questionId: string, expectedAnswers: number[], positive: boolean }[];
  attemptTimeInSeconds: number;
}
