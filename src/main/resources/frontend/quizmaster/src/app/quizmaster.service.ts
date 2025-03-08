import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {QuizInfo} from './model/quiz-info.model';
import {Quiz} from './model/quiz.model';
import {Injectable} from '@angular/core';
import {QuestionSolution} from './model/question-solution.model';
import {QuizSubmissionRequest} from './model/submission-request';
import {QuizResult} from './model/quiz-result.model';

@Injectable({providedIn: 'root'})
export class QuizmasterService {

  constructor(private http: HttpClient) {
  }

  private baseUrl = '/api';

  getQuizzesList(): Observable<QuizInfo[]> {
    return this.http.get<QuizInfo[]>(`${this.baseUrl}/quiz`);
  }

  getQuiz(id: string): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.baseUrl}/quiz/${id}`);
  }

  submitAnswers(quizId: string, answers: { [questionId: string]: number[] }): Observable<QuizResult> {
    const solutions: QuestionSolution[] = Object.keys(answers).map(questionId => ({
      questionId,
      answers: answers[questionId]
    }));
    const request: QuizSubmissionRequest = {quizId, solutions};
    return this.http.post<QuizResult>(`${this.baseUrl}/submission`, request);
  }
}
