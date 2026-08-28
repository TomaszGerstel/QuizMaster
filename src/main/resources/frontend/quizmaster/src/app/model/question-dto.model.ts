import {AnswerDTO} from './answer-dto.model';

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

export enum QuestionType {
  SINGLE_CHOICE = 'SINGLE_CHOICE',
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  TEXT = 'TEXT',
  NUMBER = 'NUMBER',
  BOOLEAN = 'BOOLEAN',
  RATING = 'RATING',
  SURVEY = 'SURVEY'
}

export enum ScoringStrategyType {
  ALL_OR_NOTHING = 'ALL_OR_NOTHING',
  PARTIAL = 'PARTIAL',
  MANUAL = 'MANUAL',
  NONE = 'NONE'
}
