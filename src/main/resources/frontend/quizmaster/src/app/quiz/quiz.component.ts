import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { QuizmasterService } from '../quizmaster.service';
import { QuizInfo } from '../model/quiz-info.model';
import { Quiz } from '../model/quiz.model';
import {QuizResult} from '../model/quiz-result.model';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.css'],
  standalone: false
})
export class QuizComponent implements OnInit {
  quizzes: QuizInfo[] = [];
  selectedQuiz: Quiz | null = null;
  selectedAnswers: { [questionId: string]: number[] } = {};
  quizResult: QuizResult | null = null;
  isSubmitDisabled: boolean = false;
  isFormDisabled: boolean = false;

  @ViewChild('resultSection') resultSection!: ElementRef;

  constructor(private quizService: QuizmasterService) { }

  ngOnInit(): void {
    this.quizService.getQuizzesList().subscribe(data => {
      this.quizzes = data;
    });
  }

  selectQuiz(id: string): void {
    this.quizService.getQuiz(id).subscribe(data => {
      this.selectedQuiz = data;
      this.selectedAnswers = {};
      this.quizResult = null;
      this.isSubmitDisabled = false;
      this.isFormDisabled = false;
    });
  }

  toggleAnswer(questionId: string, answer: number): void {
    if (!this.selectedAnswers[questionId]) {
      this.selectedAnswers[questionId] = [];
    }
    const answers = this.selectedAnswers[questionId];
    const answerIndex = answers.indexOf(answer);
    if (answerIndex > -1) {
      answers.splice(answerIndex, 1);
    } else {
      answers.push(answer);
    }
  }

  submitAnswers(): void {
    if (this.selectedQuiz) {
      this.isSubmitDisabled = true;
      this.isFormDisabled = true;
      this.quizService.submitAnswers(this.selectedQuiz.id, this.selectedAnswers).subscribe(response => {
        this.quizResult = response;
        console.log('Answers submitted successfully', response);
        this.scrollToResult();
      });
    }
  }

  resetForm(): void {
    this.selectedAnswers = {};
    this.quizResult = null;
    this.isSubmitDisabled = false;
    this.isFormDisabled = false;
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    checkboxes.forEach(checkbox => (checkbox as HTMLInputElement).checked = false);
  }

  scrollToResult(): void {
    setTimeout(() => {
      this.resultSection.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }, 0);
  }
}
