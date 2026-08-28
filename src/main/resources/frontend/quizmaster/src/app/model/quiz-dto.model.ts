import {QuestionDTO} from './question-dto.model';

export interface QuizDTO {
  id: string;
  name: string;
  description: string;
  author: string;
  type: string;
  version: number;
  questions: QuestionDTO[];
}
