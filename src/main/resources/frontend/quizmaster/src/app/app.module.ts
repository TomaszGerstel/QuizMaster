import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.compnent';
import { QuizListComponent } from './quiz/quiz-list.component';
import { QuizModalComponent } from './quiz/quiz-modal.component';
import { AboutComponent } from './about/about.component';
import { AppRoutingModule } from './app.routes';
import { HttpClientModule } from '@angular/common/http';
import {DurationFormatPipe} from "./pipe/duration-format-pipe";
import { ModalModule } from 'ngx-bootstrap/modal';

@NgModule({
  declarations: [
    AppComponent,
    QuizListComponent,
    QuizModalComponent,
    AboutComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        DurationFormatPipe,
        ModalModule.forRoot(),
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
