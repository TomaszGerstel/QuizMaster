import {Component, OnInit} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {QuizDTO} from '../model/quiz-dto.model';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {AssignQuestionsModalComponent} from './assign-questions-modal.component';
import {NewQuizModalComponent} from './new-quiz-modal.component';

@Component({
  selector: 'manage-quiz',
  templateUrl: './quiz-management.component.html',
  styleUrls: ['./quiz-management.component.css'],
  standalone: false
})
export class QuizManagementComponent implements OnInit {
  quizzes: QuizDTO[] = [];

  expandedQuizId: string | null = null;
  expandedQuestionId: string | null = null;
  selectedQuestions: {
    [quizId: string]: string[]
  } = {};

  modalRef!: BsModalRef;

  username?: string;
  email?: string;

  constructor(
    private quizService: QuizmasterService,
    private modalService: BsModalService
  ) {
  }

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.quizService.getQuizzesForManagement().subscribe(data => {
      this.quizzes = data;
    });
    this.expandedQuizId = null;
  }

  assignQuestions(id: string, name: string) {
      const initialState = {
      quizId: id,
      quizName: name
    };
    this.modalRef = this.modalService.show(AssignQuestionsModalComponent, {initialState, class: 'custom-modal'});
    const modalContent = this.modalRef.content as AssignQuestionsModalComponent;
    if (modalContent?.quizModified) {
      modalContent.quizModified.subscribe(() => {
        this.loadQuizzes(); // Reload quizzes after modification
      });
    } else {
      console.error('Modal content is undefined');
    }
  }

 createNewQuiz(): void {
   this.modalRef = this.modalService.show(NewQuizModalComponent, { class: 'custom-modal' });
   const modalContent = this.modalRef.content as NewQuizModalComponent;
   if (modalContent) {
     modalContent.quizCreated.subscribe(() => {
       this.loadQuizzes(); // Reload quizzes after creation
     });
   } else {
     console.error('Modal content is undefined');
   }
 }

 removeQuestionsFromQuiz(quizId: string, questionIds: string[]): void {
    if (questionIds.length === 0) {
      alert('Please select at least one question to remove.');
      return;
    }

    if (confirm('Are you sure you want to remove the selected questions from the quiz?')) {
      this.quizService.removeQuestionsFromQuiz(quizId, questionIds).subscribe({
        next: () => {
          alert('Selected questions removed from the quiz successfully!');
          this.loadQuizzes(); // Reload quizzes after modification
        },
        error: (err) => {
          console.error('Remove questions error:', err);
          const message = err?.error?.reason || err?.error?.message || 'Unexpected error while removing questions';
          alert(message);
        }
      });
    }
  }

 toggleQuestionSelection(
   quizId: string,
   questionId: string,
   event: any
 ): void {

   if (!this.selectedQuestions[quizId]) {
     this.selectedQuestions[quizId] = [];
   }

   if (event.target.checked) {

     this.selectedQuestions[quizId].push(questionId);

   } else {

     this.selectedQuestions[quizId] =
       this.selectedQuestions[quizId]
         .filter(id => id !== questionId);
   }
 }


}
