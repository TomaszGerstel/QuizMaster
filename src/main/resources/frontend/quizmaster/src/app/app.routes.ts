import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {QuizListComponent} from './quiz/quiz-list.component';
import {AboutComponent} from './about/about.component';

const routes: Routes = [
  {
    path: '',
    component: QuizListComponent
  },
  { path: 'about',
    component: AboutComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
