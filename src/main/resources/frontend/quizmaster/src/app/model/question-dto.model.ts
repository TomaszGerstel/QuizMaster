import {AnswerDTO} from './answer-dto.model';

export interface QuestionDTO {
  id: string;
  question: string;
  tags?: string;
  explanation?: string;
  author: string;
  answers: AnswerDTO[];
}
