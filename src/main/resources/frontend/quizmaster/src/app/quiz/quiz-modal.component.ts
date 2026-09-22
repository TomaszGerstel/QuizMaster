import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {Quiz} from '../model/quiz.model';
import {QuizResult} from '../model/quiz-result.model';
import {AnswerStatus} from '../model/answer.model';
import {Question, QuestionType, QuestionStatus, ScoringStrategyType} from '../model/question.model';
import {QuestionSolution} from '../model/question-solution.model';
import {BsModalRef} from 'ngx-bootstrap/modal';

@Component({
  templateUrl: './quiz-modal.component.html',
  styleUrls: ['./quiz-modal.component.css'],
  standalone: false
})
export class QuizModalComponent implements OnInit {
  quizId?: string;
  username?: string;
  email?: string;

  quiz: Quiz | null = null;
  userAnswers: { [questionId: string]: QuestionSolution } = {};
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
      this.getQuiz(this.quizId, this.username ? this.username : 'anonymous', this.email ? this.email : '');
    }
  }

  getQuiz(id: string, username: string, email: string): void {
    this.quizService.getQuiz(id, username, email).subscribe(data => {
      this.quiz = data;
      this.userAnswers = {};
      this.quizResult = null;
      this.isSubmitDisabled = false;
      this.isFormDisabled = false;
    });
  }

  toggleAnswer(question: Question, answerNo: number): void {
    const questionId = question.questionId;

    if (question.type === QuestionType.SINGLE_CHOICE) {
      this.userAnswers[questionId] = {
        questionId,
        answers: [answerNo]
      };
      return;
    }

    if (question.type === QuestionType.MULTIPLE_CHOICE) {
      const answers = [
        ...(this.userAnswers[questionId]?.answers ?? [])
      ];

      const index = answers.indexOf(answerNo);

      if (index >= 0) {
        answers.splice(index, 1);
      } else {
        answers.push(answerNo);
      }

      this.userAnswers[questionId] = {
        questionId,
        answers
      };
    }
  }

  submitAnswers(): void {
    if (!this.quiz) {
      return;
    }
    this.isSubmitDisabled = true;
    this.isFormDisabled = true;
    this.quizService.submitAnswers(
      this.quiz.id,
      this.quiz.sessionId,
      this.userAnswers
    ).subscribe({
      next: response => {
        this.quizResult = response;
        this.markAnswers();
        this.scrollToResult();
      },
      error: err => {
        console.error('Failed to submit answers', err);
        this.isSubmitDisabled = false;
        this.isFormDisabled = false;
      }
    });

    console.log(this.quizResult);
  }

  markAnswers(): void {
    if (!this.quiz || !this.quizResult) {
      return;
    }

    this.quiz.questions.forEach(question => {
      const report = this.quizResult!.answersReport
        .find(r => r.questionId === question.questionId);

      if (!report) {
        question.status = QuestionStatus.Initial;
        question.explanation = '';
        return;
      }

      const answered = this.isQuestionAnswered(question);

      console.log(question.scoringStrategyType, report.positive, answered);

      switch (question.scoringStrategyType) {
        case ScoringStrategyType.MANUAL:
          question.status = answered
            ? QuestionStatus.Manual
            : QuestionStatus.Failed;
          break;

        case ScoringStrategyType.NONE:
          question.status = answered
            ? QuestionStatus.Answered
            : QuestionStatus.Failed;
          break;

        default:
          question.status = report.positive
            ? QuestionStatus.Passed
            : QuestionStatus.Failed;
      }

      question.explanation = report.explanation;

      if (this.isChoiceQuestion(question)) {
        question.answers.forEach(answer => {
          answer.status = report.expectedAnswers.includes(answer.no)
            ? AnswerStatus.Correct
            : AnswerStatus.Wrong;
        });
      }
    });
  }

  setTextAnswer(questionId: string, value: string): void {
    this.userAnswers[questionId] = {
      questionId,
      textAnswer: value
    };
  }

  setNumberAnswer(questionId: string, value: string): void {
    this.userAnswers[questionId] = {
      questionId,
      numberAnswer: value === '' ? undefined : Number(value)
    };
  }

  isChoiceQuestion(question: Question): boolean {
    return question.type === QuestionType.SINGLE_CHOICE ||
           question.type === QuestionType.MULTIPLE_CHOICE;
  }

  setBooleanAnswer(questionId: string, value: boolean): void {
    this.userAnswers[questionId] = {
      questionId,
      booleanAnswer: value
    };
  }

  isSingleChoice(question: Question): boolean {
    return question.type === QuestionType.SINGLE_CHOICE;
  }

  isMultipleChoice(question: Question): boolean {
    return question.type === QuestionType.MULTIPLE_CHOICE;
  }

  ratingValues(question: Question): number[] {
    const min = question.ratingMin ?? 1;
    const max = question.ratingMax ?? 5;
    return Array.from(
      {length: max - min + 1},
      (_, i) => min + i
    );
  }

  isAnswerSelected(question: Question, answerNo: number): boolean {
    const value = this.userAnswers[question.questionId];
    if (!Array.isArray(value)) {
      return false;
    }
    return value.includes(answerNo);
  }

  get answeredQuestionsCount(): number {
    if (!this.quiz) {
      return 0;
    }
    return this.quiz.questions.filter(question =>
      this.isQuestionAnswered(question)
    ).length;
  }

  isQuestionAnswered(question: Question): boolean {
    const answer = this.userAnswers[question.questionId];

    if (!answer) return false;

    if (answer.answers !== undefined) {
      return answer.answers.length > 0;
    }
    if (answer.textAnswer !== undefined) {
      return answer.textAnswer.trim().length > 0;
    }
    if (answer.numberAnswer !== undefined) {
      return true;
    }
    if (answer.booleanAnswer !== undefined) {
      return true;
    }
    if (answer.ratingAnswer !== undefined) {
      return true;
    }
    return false;
  }

  get progressPercent(): number {
    if (!this.quiz) return 0;
    return Math.round(
      (this.answeredQuestionsCount / this.quiz.questions.length) * 100
    );
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
