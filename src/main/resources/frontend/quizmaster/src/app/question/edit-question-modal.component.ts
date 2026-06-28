import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { QuizmasterService } from '../quizmaster.service';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { QuestionDTO } from "../model/question-dto.model";
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  templateUrl: './edit-question-modal.component.html',
  styleUrls: ['./edit-question-modal.component.css'],
  standalone: false
})
export class EditQuestionModalComponent implements OnInit {
  @Output() questionUpdated = new EventEmitter<void>();

  question!: QuestionDTO;
  questionForm!: FormGroup;

  constructor(
    public bsModalRef: BsModalRef,
    private quizService: QuizmasterService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.questionForm = this.fb.group({
      question: [this.question?.question || '', Validators.required],
      explanation: [this.question?.explanation || ''],
      author: [this.question?.author || '', Validators.required],
      tags: [this.question?.tags || ''],
      answers: this.fb.array([])
    });

    if (this.question?.answers?.length) {
      this.question.answers.forEach(answer => {
        this.addAnswer(answer.value, answer.isCorrect);
      });
    } else {
      this.addAnswer();
      this.addAnswer();
    }
  }

  get answers(): FormArray {
    return this.questionForm.get('answers') as FormArray;
  }

  addAnswer(value = '', isCorrect = false): void {
    this.answers.push(
      this.fb.group({
        value: [value, Validators.required],
        isCorrect: [isCorrect]
      })
    );
  }

  removeAnswer(index: number): void {
    if (this.answers.length > 2) {
      this.answers.removeAt(index);
    }
  }

  saveChanges(): void {
    if (this.questionForm.invalid) {
      this.questionForm.markAllAsTouched();
      return;
    }

    const payload: QuestionDTO = {
      ...this.question,
      ...this.questionForm.value
    };

    if (payload.id) {
      this.quizService.updateQuestion(payload.id, payload).subscribe({
        next: () => {
          this.questionUpdated.emit();
          this.bsModalRef.hide();
        },
        error: (err: HttpErrorResponse) => {
          console.error(err);
          alert(err?.error?.message || 'Error');
        }
      });
    } else {
      this.quizService.createQuestion(payload).subscribe({
        next: () => {
          this.questionUpdated.emit();
          this.bsModalRef.hide();
        },
        error: (err: HttpErrorResponse) => {
          console.error(err);
          alert(err?.error?.message || 'Error');
        }
      });
    }
  }

  cancel(): void {
    this.bsModalRef.hide();
  }
}
