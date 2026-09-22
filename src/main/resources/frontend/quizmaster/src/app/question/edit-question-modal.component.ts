import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormArray,
  Validators
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { BsModalRef } from 'ngx-bootstrap/modal';

import { QuizmasterService } from '../quizmaster.service';
import {QuestionDTO} from '../model/question-dto.model';
import {QuestionType, ScoringStrategyType} from "../model/question.model";

@Component({
  templateUrl: './edit-question-modal.component.html',
  styleUrls: ['./edit-question-modal.component.css'],
  standalone: false
})
export class EditQuestionModalComponent implements OnInit {

  readonly QuestionType = QuestionType;
  readonly ScoringStrategyType = ScoringStrategyType;

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
        ScoringStrategyType.ALL_OR_NOTHING,
        Validators.required
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

    this.updateForQuestionType();

    this.questionForm
      .get('type')!
      .valueChanges
      .subscribe(() => {
        this.updateForQuestionType();
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

  /**
   * Returns scoring strategies allowed for a given question type.
   */
  getScoringStrategies(type: QuestionType): ScoringStrategyType[] {
    switch (type) {
      case QuestionType.SINGLE_CHOICE:
        return [
          ScoringStrategyType.ALL_OR_NOTHING
        ];

      case QuestionType.MULTIPLE_CHOICE:
        return [
          ScoringStrategyType.ALL_OR_NOTHING,
          ScoringStrategyType.PARTIAL
        ];

      case QuestionType.TEXT:
        return [
          ScoringStrategyType.MANUAL
        ];

      case QuestionType.NUMBER:
      case QuestionType.BOOLEAN:
        return [
          ScoringStrategyType.ALL_OR_NOTHING
        ];

      case QuestionType.RATING:
      case QuestionType.SURVEY:
        return [
          ScoringStrategyType.NONE
        ];
    }
  }

  getScoringStrategyLabel(strategy: ScoringStrategyType): string {
    switch (strategy) {
      case ScoringStrategyType.ALL_OR_NOTHING:
        return 'All or nothing';

      case ScoringStrategyType.PARTIAL:
        return 'Partial';

      case ScoringStrategyType.MANUAL:
        return 'Manual grading';

      case ScoringStrategyType.NONE:
        return 'Not scored';
    }
  }

  /**
   * Adjusts form fields and scoring strategy according to question type.
   */
  private updateForQuestionType(): void {
    const type = this.questionType;

    this.updateScoringStrategy(type);
    this.updateAnswers(type);
    this.updateValidators(type);
  }

  /**
   * Makes sure the selected scoring strategy is valid
   * for the current question type.
   */
  private updateScoringStrategy(type: QuestionType): void {
    const control = this.questionForm.get('scoringStrategyType');

    if (!control) {
      return;
    }

    const allowedStrategies = this.getScoringStrategies(type);
    const currentStrategy = control.value;

    if (!allowedStrategies.includes(currentStrategy)) {
      control.setValue(allowedStrategies[0], {
        emitEvent: false
      });
    }

    // If only one strategy is possible, disable the selector.
    if (allowedStrategies.length === 1) {
      control.disable({ emitEvent: false });
    } else {
      control.enable({ emitEvent: false });
    }
  }

  /**
   * Initializes/clears answers depending on question type.
   */
  private updateAnswers(type: QuestionType): void {
    if (
      type !== QuestionType.SINGLE_CHOICE &&
      type !== QuestionType.MULTIPLE_CHOICE
    ) {
      this.answers.clear();
      return;
    }

    // Do not recreate answers if they are already present.
    if (this.answers.length > 0) {
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

  /**
   * Updates validators specific to the selected question type.
   */
  private updateValidators(type: QuestionType): void {
    const expectedAnswer = this.questionForm.get('expectedAnswer');
    const correctNumber = this.questionForm.get('correctNumber');
    const tolerance = this.questionForm.get('tolerance');
    const correctBoolean = this.questionForm.get('correctBoolean');
    const ratingMin = this.questionForm.get('ratingMin');
    const ratingMax = this.questionForm.get('ratingMax');

    // Clear validators first.
    expectedAnswer?.clearValidators();
    correctNumber?.clearValidators();
    tolerance?.clearValidators();
    correctBoolean?.clearValidators();
    ratingMin?.clearValidators();
    ratingMax?.clearValidators();

    switch (type) {
      case QuestionType.TEXT:
        expectedAnswer?.setValidators([
          Validators.required
        ]);
        break;

      case QuestionType.NUMBER:
        correctNumber?.setValidators([
          Validators.required
        ]);

        tolerance?.setValidators([
          Validators.min(0)
        ]);
        break;

      case QuestionType.BOOLEAN:
        correctBoolean?.setValidators([
          Validators.required
        ]);
        break;

      case QuestionType.RATING:
        ratingMin?.setValidators([
          Validators.required,
          Validators.min(1)
        ]);

        ratingMax?.setValidators([
          Validators.required,
          Validators.min(1)
        ]);
        break;
    }

    expectedAnswer?.updateValueAndValidity({ emitEvent: false });
    correctNumber?.updateValueAndValidity({ emitEvent: false });
    tolerance?.updateValueAndValidity({ emitEvent: false });
    correctBoolean?.updateValueAndValidity({ emitEvent: false });
    ratingMin?.updateValueAndValidity({ emitEvent: false });
    ratingMax?.updateValueAndValidity({ emitEvent: false });
  }

  addAnswer(value = '', isCorrect = false): void {
    this.answers.push(
      this.fb.group({
        value: [
          value,
          Validators.required
        ],
        isCorrect: [
          isCorrect
        ]
      })
    );
  }

  removeAnswer(index: number): void {
    if (this.answers.length > 2) {
      this.answers.removeAt(index);
    }
  }

  /**
   * Used for SINGLE_CHOICE.
   */
  setCorrectAnswer(index: number): void {
    if (!this.isSingleChoice) {
      return;
    }

    this.answers.controls.forEach((control, i) => {
      control.patchValue({
        isCorrect: i === index
      });
    });
  }

  /**
   * Used when changing question type from template,
   * if you want an explicit handler there.
   */
  onQuestionTypeChanged(type: QuestionType): void {
    this.questionForm.patchValue(
      { type },
      { emitEvent: true }
    );
  }

  saveChanges(): void {
    if (this.questionForm.invalid) {
      this.questionForm.markAllAsTouched();
      return;
    }

    const payload: QuestionDTO = {
      ...this.question,
      ...this.questionForm.getRawValue()
    };

    /*
     * The type is disabled during edit, therefore getRawValue()
     * is important here.
     */

    if (!this.isChoiceQuestion) {
      payload.answers = [];
    }

    /*
     * Remove fields that do not belong to the selected type.
     */
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

    /*
     * Make sure the strategy is always valid.
     */
    const allowedStrategies = this.getScoringStrategies(payload.type);

    if (!allowedStrategies.includes(payload.scoringStrategyType)) {
      payload.scoringStrategyType = allowedStrategies[0];
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
