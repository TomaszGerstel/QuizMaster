import {AnswerDTO} from './answer-dto.model';
import {QuestionType, ScoringStrategyType} from './question.model';

export interface QuestionDTO {
  id?: string;
  question: string;
  explanation?: string;
  author: string;
  tags?: string;

  type: QuestionType;
  scoringStrategyType: ScoringStrategyType;

  answers: AnswerDTO[];

  expectedAnswer?: string;
  correctNumber?: number;
  tolerance?: number;
  correctBoolean?: boolean;

  ratingMin?: number;
  ratingMax?: number;

  version?: number;
}
