import {Answer} from './answer.model';

export interface Question {
  questionId: string;
  question: string;

  type: QuestionType;

  answers: Answer[];
  scoringStrategyType: ScoringStrategyType;

  expectedAnswer?: string;
  correctNumber?: number;
  tolerance?: number;
  correctBoolean?: boolean;

  ratingMin?: number;
  ratingMax?: number;

  status?: QuestionStatus;
  explanation?: string;
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

export enum QuestionStatus {
  Initial = 'INITIAL',
  Passed = 'PASSED',
  Failed = 'FAILED',
  Manual = 'MANUAL',
  Answered = 'ANSWERED'
}

export enum ScoringStrategyType {
  ALL_OR_NOTHING = 'ALL_OR_NOTHING',
  PARTIAL = 'PARTIAL',
  MANUAL = 'MANUAL',
  NONE = 'NONE'
}
