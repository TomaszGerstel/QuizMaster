import {Answer} from './answer.model';

export interface Question {
  questionId: string;
  question: string;
  answers: Answer[];
  status?: QuestionStatus | QuestionStatus.Initial;
  explanation?: string;
}

export enum QuestionStatus {
  Initial = 'Initial',
  Failed = 'Failed',
  Passed = 'Passed'
}
