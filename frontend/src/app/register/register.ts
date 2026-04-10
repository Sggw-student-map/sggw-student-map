import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onRegister(
    firstName: string,
    lastName: string,
    username: string,
    email: string,
    password: string
  ): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!firstName.trim() || !lastName.trim() || !username.trim() || !email.trim() || !password.trim()) {
      this.errorMessage = 'Wypelnij wszystkie pola.';
      return;
    }

    this.isLoading = true;
    this.authService
      .register({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        username: username.trim(),
        email: email.trim(),
        password: password.trim()
      })
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Konto utworzone. Za chwile nastapi przekierowanie do logowania.';
          setTimeout(() => this.router.navigate(['/login']), 900);
        },
        error: () => {
          this.isLoading = false;
          this.errorMessage = 'Nie udalo sie zalozyc konta. Sprawdz dane lub sprobuj ponownie.';
        }
      });
  }
}
