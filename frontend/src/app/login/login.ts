import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  isLoading = false;
  errorMessage = '';

  constructor(
    private readonly router: Router,
    private readonly authService: AuthService
  ) {}

  onLogin(username: string, password: string): void {
    this.errorMessage = '';

    if (!username.trim() || !password.trim()) {
      this.errorMessage = 'Wpisz login i hasło.';
      return;
    }

    this.isLoading = true;
    this.authService.login({ username: username.trim(), password: password.trim() }).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/map']);
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Niepoprawne dane logowania albo niedostępny backend.';
      }
    });
  }

}
