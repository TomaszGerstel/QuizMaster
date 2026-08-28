import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { QuizmasterService } from '../quizmaster.service';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { QuestionDTO, QuestionType, ScoringStrategyType} from "../model/question-dto.model";
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
      question: [
        this.question?.question || '',
        Validators.required
      ],

      explanation: [
        this.question?.explanation || ''
      ],

      author: [
        this.question?.author || '',
        Validators.required
      ],

      tags: [
        this.question?.tags || ''
      ],

      type: [
        {
          value: this.question?.type || QuestionType.SINGLE_CHOICE,
          disabled: !!this.question?.id
        },
        Validators.required
      ],

      scoringStrategyType: [
        this.question?.scoringStrategyType ||
        ScoringStrategyType.ALL_OR_NOTHING
      ],

      answers: this.fb.array([]),

      expectedAnswer: [
        this.question?.expectedAnswer || ''
      ],

      correctNumber: [
        this.question?.correctNumber ?? null
      ],

      tolerance: [
        this.question?.tolerance ?? null
      ],

      correctBoolean: [
        this.question?.correctBoolean ?? null
      ],

      ratingMin: [
        this.question?.ratingMin ?? 1
      ],

      ratingMax: [
        this.question?.ratingMax ?? 5
      ]
    });

    this.initializeAnswers();
    this.updateScoringStrategy();

    this.questionForm
      .get('type')!
      .valueChanges
      .subscribe(type => {
        if (
          type !== QuestionType.SINGLE_CHOICE &&
          type !== QuestionType.MULTIPLE_CHOICE
        ) {
          this.answers.clear();
        }
        this.updateScoringStrategy();
      });
  }

  get answers(): FormArray {
    return this.questionForm.get('answers') as FormArray;
  }

  get questionType(): QuestionType {
    return this.questionForm.get('type')?.value;
  }

  get isChoiceQuestion(): boolean {
    return this.questionType === QuestionType.SINGLE_CHOICE ||
           this.questionType === QuestionType.MULTIPLE_CHOICE;
  }

  get isSingleChoice(): boolean {
    return this.questionType === QuestionType.SINGLE_CHOICE;
  }

  get isMultipleChoice(): boolean {
    return this.questionType === QuestionType.MULTIPLE_CHOICE;
  }

  private initializeAnswers(): void {
    if (!this.isChoiceQuestion) {
      return;
    }

  if (this.question?.answers?.length) {
    this.question.answers.forEach(answer => {
      this.addAnswer(answer.value, answer.isCorrect);
    });
  } else {
    this.addAnswer();
    this.addAnswer();
    }
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

  setCorrectAnswer(index: number): void {
    this.answers.controls.forEach((control, i) => {
      control.patchValue({
        isCorrect: i === index
      });
    });
  }

  private updateScoringStrategy(): void {
    const control = this.questionForm.get('scoringStrategyType');

    switch (this.questionType) {

      case QuestionType.SINGLE_CHOICE:
        control?.setValue(
          this.question?.scoringStrategyType ||
          ScoringStrategyType.ALL_OR_NOTHING,
          { emitEvent: false }
        );
        break;

      case QuestionType.MULTIPLE_CHOICE:
        control?.setValue(
          this.question?.scoringStrategyType ||
          ScoringStrategyType.ALL_OR_NOTHING,
          { emitEvent: false }
        );
        break;

      case QuestionType.TEXT:
        control?.setValue(
          ScoringStrategyType.MANUAL,
          { emitEvent: false }
        );
        break;

      case QuestionType.NUMBER:
        control?.setValue(
          ScoringStrategyType.ALL_OR_NOTHING,
          { emitEvent: false }
        );
        break;

      case QuestionType.BOOLEAN:
        control?.setValue(
          ScoringStrategyType.ALL_OR_NOTHING,
          { emitEvent: false }
        );
        break;

      case QuestionType.RATING:
      case QuestionType.SURVEY:
        control?.setValue(
          ScoringStrategyType.NONE,
          { emitEvent: false }
        );
        break;
    }
  }

  saveChanges(): void {
    if (this.questionForm.invalid) {
      this.questionForm.markAllAsTouched();
      console.log("invalid")
      Object.keys(this.questionForm.controls).forEach(key => {
        const control = this.questionForm.get(key);

        if (control?.invalid) {
          console.log('INVALID:', key, control.errors, control.value);
        }
      });
      return;
    }
  console.log("valid")


    const payload: QuestionDTO = {
      ...this.question,
      ...this.questionForm.getRawValue()
    };

    if (!this.isChoiceQuestion) {
      payload.answers = [];
    }

    if (payload.type !== QuestionType.TEXT) {
      payload.expectedAnswer = undefined;
    }

    if (payload.type !== QuestionType.NUMBER) {
      payload.correctNumber = undefined;
      payload.tolerance = undefined;
    }

    if (payload.type !== QuestionType.BOOLEAN) {
      payload.correctBoolean = undefined;
    }

    if (payload.type !== QuestionType.RATING) {
      payload.ratingMin = undefined;
      payload.ratingMax = undefined;
    }

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
