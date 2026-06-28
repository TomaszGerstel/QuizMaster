import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {QuizListComponent} from './quiz/quiz-list.component';
import {AboutComponent} from './about/about.component';
import {QuizManagementComponent} from './manage/quiz-management.component';
import {QuestionManagementComponent} from './question/question-management.component';

const routes: Routes = [
  {
    path: '',
    component: QuizListComponent
  },
  { path: 'about',
    component: AboutComponent
  },
  { path: 'manage-quizzes',
    component: QuizManagementComponent
  },
  { path: 'manage-questions',
    component: QuestionManagementComponent
  },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
