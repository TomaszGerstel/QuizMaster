export interface QuizResult {
  quizId: string;
  isPositive: boolean;
  quizScore: number;
  percentageScore: number;
  questionsCount: number;
  answersReport: { questionId: string, expectedAnswers: number[], explanation: string, positive: boolean }[];
  attemptTimeInSeconds: number;
}
