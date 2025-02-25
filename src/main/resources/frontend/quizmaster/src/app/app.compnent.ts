import {Component, inject, OnInit} from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false,
})
export class AppComponent implements OnInit {
  title = 'QuizMaster';
  private readonly router = inject(Router)

  ngOnInit(): void {
    console.log('app component loaded');
  }
}
