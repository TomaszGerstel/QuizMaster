import {QuestionDTO} from './question-dto.model';

export interface QuizDTO {
  id: string;
  name: string;
  questions: QuestionDTO[];
}
