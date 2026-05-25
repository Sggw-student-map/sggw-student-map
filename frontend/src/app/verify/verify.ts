import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

import { environment } from '../../environments/environment';

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './verify.html',
  styleUrls: ['./verify.css']
})
export class Verify implements OnInit {
  status: 'loading' | 'success' | 'error' = 'loading';
  message: string = 'Trwa weryfikacja Twojego konta...';

  constructor(private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      
      if (token) {
        this.verifyAccount(token);
      } else {
        this.status = 'error';
        this.message = 'Brak tokenu weryfikacyjnego w linku.';
      }
    });
  }

  verifyAccount(token: string): void {
    this.http.get(`${environment.authBaseUrl}/confirm?token=${token}`, { responseType: 'text' })
      .subscribe({
        next: (response) => {
          this.status = 'success';
          this.message = 'Twoje konto zostało pomyślnie aktywowane!';
        },
        error: (err) => {
          this.status = 'error';
          this.message = 'Link wygasł lub jest nieprawidłowy.';
        }
      });
  }
}