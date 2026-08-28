export interface CreateQuizRequest {
  name: string;
  description?: string;
  type: string;
  passRate?: number;
  author: string;
  questionIds: []
}
