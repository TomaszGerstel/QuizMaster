import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {QuizInfo} from '../model/quiz-info.model';
import {Quiz} from '../model/quiz.model';
import {QuizResult} from '../model/quiz-result.model';
import {AnswerStatus} from '../model/answer.model';
import {QuestionStatus} from '../model/question.model';

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

  constructor(private quizService: QuizmasterService) {
  }

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
      this.quizService.submitAnswers(this.selectedQuiz.id, this.selectedQuiz.sessionId, this.selectedAnswers)
          .subscribe(response => {
        this.quizResult = response;
        this.markAnswers();
        console.log('Answers submitted successfully', response);
        this.scrollToResult();
      });
    }
  }

  markAnswers(): void {
    if (this.selectedQuiz && this.quizResult) {
      this.selectedQuiz.questions.forEach(question => {
        const reportForQuestion = this.quizResult ?
          this.quizResult.answersReport.find(expectedAnswers => expectedAnswers.questionId === question.id) : null;

        reportForQuestion ?
          (question.status = reportForQuestion.positive ? QuestionStatus.Passed : QuestionStatus.Failed)
          : QuestionStatus.Initial;

        question.answers.forEach(answer => {
          if (reportForQuestion?.expectedAnswers.includes(answer.no)) {
            answer.status = AnswerStatus.Correct;
          } else {
            answer.status = AnswerStatus.Wrong;
          }
        });
      });
    }
  }

  resetQuiz(): void {
    this.selectQuiz(this.selectedQuiz? this.selectedQuiz.id : '0');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  scrollToResult(): void {
    setTimeout(() => {
      this.resultSection.nativeElement.scrollIntoView({behavior: 'smooth'});
    }, 0);
  }
}
