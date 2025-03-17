import {Question} from './question.model';

export interface Quiz {
  id: string;
  title: string;
  sessionId: string;
  questions: Question[];
}
