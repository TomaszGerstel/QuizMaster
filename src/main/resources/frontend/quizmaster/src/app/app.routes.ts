import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {QuizListComponent} from './quiz/quiz-list.component';
import {AboutComponent} from './about/about.component';
import {QuizManagementComponent} from './manage/quiz-management.component';

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
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
