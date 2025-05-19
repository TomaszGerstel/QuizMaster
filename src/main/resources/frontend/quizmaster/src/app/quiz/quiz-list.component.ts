import {Component, OnInit} from '@angular/core';
import {QuizmasterService} from '../quizmaster.service';
import {QuizInfo} from '../model/quiz-info.model';
import {BsModalService} from 'ngx-bootstrap/modal';
import {QuizModalComponent} from './quiz-modal.component';

@Component({
  selector: 'app-quiz',
  templateUrl: './quiz-list.component.html',
  styleUrls: ['./quiz-list.component.css'],
  standalone: false
})
export class QuizListComponent implements OnInit {
  quizzes: QuizInfo[] = [];
  expandedQuizId: string | null = null;

  constructor(
    private quizService: QuizmasterService,
    private modalService: BsModalService
  ) {
  }

  ngOnInit(): void {
    this.quizService.getQuizzesList().subscribe(data => {
      this.quizzes = data;
    });
  }

  startQuiz(id: string) {
    const initialState = {
      quizId: id
    };
    this.modalService.show(QuizModalComponent, {initialState, class: 'custom-modal'})
  }

}
