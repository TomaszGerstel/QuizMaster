export interface Answer {
  no: number;
  content: string;
  status?: AnswerStatus | AnswerStatus.Initial;
}

export enum AnswerStatus {
  Initial = 'Initial',
  Wrong = 'Wrong',
  Correct = 'Correct'
}
