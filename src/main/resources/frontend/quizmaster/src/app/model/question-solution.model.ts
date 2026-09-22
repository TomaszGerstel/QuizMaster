export interface QuestionSolution {
  questionId: string;
  answers?: number[];
  textAnswer?: string;
  numberAnswer?: number;
  booleanAnswer?: boolean;
  ratingAnswer?: number;
}
