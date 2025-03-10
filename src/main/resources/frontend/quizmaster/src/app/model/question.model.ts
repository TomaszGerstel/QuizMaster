import {Answer} from './answer.model';

export interface Question {
  id: string;
  question: string;
  answers: Answer[];
  status?: QuestionStatus | QuestionStatus.Initial;
}

export enum QuestionStatus {
  Initial = 'Initial',
  Failed = 'Failed',
  Passed = 'Passed'
}
