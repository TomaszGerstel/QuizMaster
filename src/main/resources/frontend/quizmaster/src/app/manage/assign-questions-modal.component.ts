import {Component, OnInit, EventEmitter, Output} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {QuestionDTO} from "../model/question-dto.model";
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  templateUrl: './assign-questions-modal.component.html',
  styleUrls: ['./assign-questions-modal.component.css'],
  standalone: false
})
export class AssignQuestionsModalComponent implements OnInit {
  @Output() quizModified = new EventEmitter<void>();

  quizId?: string;
  quizName?: string;

  questions: QuestionDTO[] = [];
  selectedQuestionIds: Set<string> = new Set<string>();

  expandedQuestionId: string | null = null;

  tagSearch = '';

  constructor(
    public bsModalRef: BsModalRef,
    private quizService: QuizmasterService) {
  }

  ngOnInit(): void {
    if (this.quizId) {
      this.loadAllQuestions();
    }
  }

  loadAllQuestions(): void {
    this.quizService.getQuestions(undefined, 'ASSIGNABLE', this.quizId).subscribe({
        next: (questions: QuestionDTO[]) => {
          this.questions = questions;
        }
      });
  }

  searchQuestionsByTag(): void {
    if (!this.tagSearch.trim()) {
      this.loadAllQuestions();
      return;
    }
    this.quizService
      .getQuestions(this.tagSearch, 'ASSIGNABLE')
      .subscribe({
        next: (questions: QuestionDTO[]) => {
          this.questions = questions;
        },
        error: (err: HttpErrorResponse) => {
          console.error(err);
        }
      });
  }

  toggleQuestionSelection(questionId: string, event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.selectedQuestionIds.add(questionId);
    } else {
      this.selectedQuestionIds.delete(questionId);
    }
  }

  assignSelectedQuestions(): void {
    if (this.quizId && this.selectedQuestionIds.size > 0) {
      const selectedIds = Array.from(this.selectedQuestionIds);
      this.quizService.assignQuestionsToQuiz(this.quizId, selectedIds).subscribe({
        next: response => {
          alert('Questions successfully assigned to the quiz!');
          this.quizModified.emit();
          this.bsModalRef.hide();
        },
        error: (err) => {
          console.error('Assign questions error:', err);
          const message = err?.error?.reason || err?.error?.message || 'Unexpected error while assigning questions';
          alert(message);
        }
      });
    } else {
      alert('Please select at least one question to assign.');
    }
  }

  cancel(): void {
    this.bsModalRef.hide();
  }
}
