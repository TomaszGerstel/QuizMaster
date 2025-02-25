import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuizInfo } from './model/quiz-info.model';
import {Quiz} from './model/quiz.model';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class QuizmasterService {

  constructor(private http: HttpClient) { }
  private baseUrl = 'http://localhost:8080/quiz';

  getQuizzesList(): Observable<QuizInfo[]> {
    console.log('getQuizzesList');
    return this.http.get<QuizInfo[]>(`${this.baseUrl}`)
  }

  getQuiz(id: string): Observable<Quiz> {
    return this.http.get<Quiz>(`${this.baseUrl}/${id}`);
  }
}
