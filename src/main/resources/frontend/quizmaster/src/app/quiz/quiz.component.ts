import { Component, OnInit } from '@angular/core';
import { QuizmasterService } from '../quizmaster.service';
import { QuizInfo } from '../model/quiz-info.model';
import { Quiz } from '../model/quiz.model';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.css'],
  standalone: false
})
export class QuizComponent implements OnInit {
  quizzes: QuizInfo[] = [];
  selectedQuiz: Quiz | null = null;

  constructor(private quizService: QuizmasterService) { }

  ngOnInit(): void {
    console.log('ngOnInit');
    this.quizService.getQuizzesList().subscribe(data => {
      this.quizzes = data;
    });
  }

  selectQuiz(id: string): void {
    this.quizService.getQuiz(id).subscribe(data => {
      this.selectedQuiz = data;
    });
  }
}
