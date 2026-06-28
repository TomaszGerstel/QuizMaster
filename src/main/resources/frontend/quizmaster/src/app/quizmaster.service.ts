import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {QuizInfo} from './model/quiz-info.model';
import {Quiz} from './model/quiz.model';
import {Injectable} from '@angular/core';
import {QuestionSolution} from './model/question-solution.model';
import {QuizSubmissionRequest} from './model/submission-request';
import {StartQuizRequest} from './model/start-quiz-request';
import {QuizResult} from './model/quiz-result.model';
import {environment} from '../environments/environment';
import {QuestionDTO} from "./model/question-dto.model";
import {QuizDTO} from "./model/quiz-dto.model";

@Injectable({providedIn: 'root'})
export class QuizmasterService {

  constructor(private http: HttpClient) {
  }

  private baseUrl = environment.apiUrl;

  getQuizzesList(): Observable<QuizInfo[]> {
    return this.http.get<QuizInfo[]>(`${this.baseUrl}/quiz`);
  }

  getQuiz(quizId: string, name: string, email: string): Observable<Quiz> {
    const params: StartQuizRequest = {quizId, name, email};
    return this.http.post<Quiz>(`${this.baseUrl}/quiz/start`, params);
  }

  submitAnswers(quizId: string, sessionId: string, answers: {
    [questionId: string]: number[]
  }): Observable<QuizResult> {
    const solutions: QuestionSolution[] = Object.keys(answers).map(questionId => ({
      questionId,
      answers: answers[questionId]
    }));
    const request: QuizSubmissionRequest = {quizId, sessionId, solutions};
    return this.http.post<QuizResult>(`${this.baseUrl}/submission`, request);
  }

  getQuizzesForManagement(): Observable<QuizDTO[]> {
    return this.http.get<QuizDTO[]>(`${this.baseUrl}/quiz/manage`);
  }

  getQuestions(tag?: string, mode?: 'ASSIGNABLE' | 'EDITABLE'): Observable<QuestionDTO[]> {
    let params: any = {};
    if (tag) { params.tag = tag; }
    if (mode) { params.mode = mode; }
    return this.http.get<QuestionDTO[]>(`${this.baseUrl}/question`, { params });
  }

  assignQuestionsToQuiz(quizId: string, questionIds: string[]): Observable<void> {
    const request = {quizId, questionIds};
    return this.http.post<void>(`${this.baseUrl}/quiz/assign-questions`, request);
  }

  createQuiz(quizName: string, author: string): Observable<{quizId: string}> {
    const request = {name: quizName, author: author, questionIds: []};
    return this.http.post<{quizId: string}>(`${this.baseUrl}/quiz/manage/new`, request);
  }

  removeQuestionsFromQuiz(quizId: string, questionIds: string[]): Observable<void> {
    const request = {quizId, questionIds};
    return this.http.post<void>(`${this.baseUrl}/quiz/remove-questions`, request);
  }

  updateQuestion(questionId: string, question: QuestionDTO): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/question/${questionId}`, question);
  }

  createQuestion(question: QuestionDTO): Observable<{questionId: string}> {
    return this.http.post<{questionId: string}>(`${this.baseUrl}/question`, question);
  }

}
