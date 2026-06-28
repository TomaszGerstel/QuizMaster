import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.compnent';
import { QuizListComponent } from './quiz/quiz-list.component';
import { QuizModalComponent } from './quiz/quiz-modal.component';
import { QuizManagementComponent } from './manage/quiz-management.component';
import { AssignQuestionsModalComponent } from './manage/assign-questions-modal.component';
import { NewQuizModalComponent } from './manage/new-quiz-modal.component';
import { QuestionManagementComponent } from './question/question-management.component';
import { EditQuestionModalComponent } from './question/edit-question-modal.component';
import { AboutComponent } from './about/about.component';
import { AppRoutingModule } from './app.routes';
import { HttpClientModule } from '@angular/common/http';
import { DurationFormatPipe } from "./pipe/duration-format-pipe";
import { ModalModule } from 'ngx-bootstrap/modal';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    QuizListComponent,
    QuizModalComponent,
    QuizManagementComponent,
    AssignQuestionsModalComponent,
    NewQuizModalComponent,
    QuestionManagementComponent,
    EditQuestionModalComponent,
    AboutComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        DurationFormatPipe,
        ModalModule.forRoot(),
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
