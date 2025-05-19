import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {Quiz} from '../model/quiz.model';
import {QuizResult} from '../model/quiz-result.model';
import {AnswerStatus} from '../model/answer.model';
import {QuestionStatus} from '../model/question.model';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  templateUrl: './quiz-modal.component.html',
  styleUrls: ['./quiz-modal.component.css'],
  standalone: false
})
export class QuizModalComponent implements OnInit {
  quizId?: string;
  quiz: Quiz | null = null;
  selectedAnswers: { [questionId: string]: number[] } = {};
  quizResult: QuizResult | null = null;
  isSubmitDisabled: boolean = false;
  isFormDisabled: boolean = false;

  @ViewChild('resultSection') resultSection!: ElementRef;

  constructor(
    public bsModalRef: BsModalRef,
    private quizService: QuizmasterService) {
  }

  ngOnInit(): void {
    if (this.quizId) {
      this.getQuiz(this.quizId);
    }
  }

  getQuiz(id: string): void {
    this.quizService.getQuiz(id).subscribe(data => {
      this.quiz = data;
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
    if (this.quiz) {
      this.isSubmitDisabled = true;
      this.isFormDisabled = true;
      this.quizService.submitAnswers(this.quiz.id, this.quiz.sessionId, this.selectedAnswers)
        .subscribe(response => {
          this.quizResult = response;
          this.markAnswers();
          console.log('Answers submitted successfully', response);
          this.scrollToResult();
        });
    }
  }

  markAnswers(): void {
    if (this.quiz && this.quizResult) {
      this.quiz.questions.forEach(question => {
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
    this.getQuiz(this.quiz ? this.quiz.id : '0');
    const modalBody = document.querySelector('.modal-dialog');
    if (modalBody?.parentElement) {
      modalBody.parentElement.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  scrollToResult(): void {
    setTimeout(() => {
      this.resultSection.nativeElement.scrollIntoView({behavior: 'smooth'});
    }, 0);
  }

  cancel(): void {
    this.bsModalRef.hide();
  }
}
