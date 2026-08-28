import {Component} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {QuestionDTO, QuestionType, ScoringStrategyType} from '../model/question-dto.model';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EditQuestionModalComponent} from './edit-question-modal.component';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'manage-question',
  templateUrl: './question-management.component.html',
  styleUrls: ['./question-management.component.css'],
  standalone: false
})
export class QuestionManagementComponent {
  questions: QuestionDTO[] = [];
  questionToEdit?: QuestionDTO;
  tagSearch = '';

  newQuestion: QuestionDTO = {
    id: '',
    question: '',
    explanation: '',
    author: '',
    tags: '',
    type: QuestionType.MULTIPLE_CHOICE,
    scoringStrategyType: ScoringStrategyType.ALL_OR_NOTHING,
    answers: [{
      value: '',
      isCorrect: false
      },
      {
        value: '',
        isCorrect: false
        },
      ],
    expectedAnswer: '',
    correctNumber: undefined,
    tolerance: undefined,
    correctBoolean: undefined,
    ratingMin: undefined,
    ratingMax: undefined,
    version: undefined
  };

  modalRef!: BsModalRef;

  constructor(
    private quizService: QuizmasterService,
    private modalService: BsModalService
  ) {
  }

  createNewQuestion(): void {
    const initialState = { question: this.newQuestion };
    this.modalRef = this.modalService.show(EditQuestionModalComponent, { initialState, class: 'custom-modal' });
    const modalContent = this.modalRef.content as EditQuestionModalComponent;
    if (modalContent) {
      modalContent.questionUpdated.subscribe(() => {
        this.loadQuestions();
      });
    } else {
      console.error('Modal content is undefined');
    }
  }

  loadQuestions(): void {
    this.quizService.getQuestions(this.tagSearch, 'EDITABLE').subscribe({
      next: (questions: QuestionDTO[]) => {
        this.questions = questions;
      },
      error: (err: HttpErrorResponse) => {
        console.error(err);
      }
    });
  }

  editQuestion(question: QuestionDTO): void {
    this.questionToEdit = question;
    const initialState = { question: this.questionToEdit };
    const modalRef = this.modalService.show(EditQuestionModalComponent, { initialState, class: 'custom-modal' });
    const modalContent = modalRef.content as EditQuestionModalComponent;
    if (modalContent) {
      modalContent.questionUpdated.subscribe(() => {
        this.loadQuestions(); // Reload questions after update
      });
    } else {
      console.error('Modal content is undefined');
    }
  }
}
