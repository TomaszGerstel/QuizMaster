import {Question} from './question.model';

export interface Quiz {
  id: string;
  title: string;
  questions: Question[];
}
