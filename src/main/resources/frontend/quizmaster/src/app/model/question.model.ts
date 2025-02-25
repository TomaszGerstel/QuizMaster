import {Answer} from './answer.model';

export interface Question {
  id: string;
  question: string;
  answers: Answer[];
}
