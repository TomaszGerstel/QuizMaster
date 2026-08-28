import {Component, OnInit, EventEmitter, Output} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {BsModalRef} from 'ngx-bootstrap/modal';
import {QuestionDTO} from "../model/question-dto.model";

@Component({
  templateUrl: './new-quiz-modal.component.html',
  styleUrls: ['./new-quiz-modal.component.css'],
  standalone: false
})
export class NewQuizModalComponent implements OnInit {
  @Output() quizCreated = new EventEmitter<void>();

  quizName?: string;
  authorName?: string;
  description?: string;
  type: string = "EXAM";
  passRate?: number;

  quizId?: string;

  constructor(
    public bsModalRef: BsModalRef,
    private quizService: QuizmasterService) {
  }

  ngOnInit(): void {
  }

  createQuiz(): void {
    if (this.quizName && this.authorName && this.type) {
      this.quizService.createQuiz(
              this.quizName, this.authorName, this.type, this.passRate, this.description
          ).subscribe({
        next: response => {
          this.quizId = response.quizId;
          alert('Quiz successfully created!');
          this.quizCreated.emit();
          this.bsModalRef.hide();
        },
        error: (err) => {
          console.error('Create quiz error:', err);
          const message = err?.error?.reason || err?.error?.message || 'Unexpected error while creating quiz';
          alert(message);
        }
      });
    } else {
      alert('Please provide all required fields (quiz name, quiz type and author name).');
    }
  }

  cancel(): void {
    this.bsModalRef.hide();
  }
}
