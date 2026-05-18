import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private fb: FormBuilder
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.maxLength(100)]],
      username: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(25)]],
      confirmPassword: ['', [Validators.required]]
    }, { 
      validators: this.passwordsMatchValidator
    });
  }

  private passwordsMatchValidator(control: AbstractControl) {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      control.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      return null;
    }
  }

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    const { confirmPassword, ...requestData } = this.registerForm.value;

    this.authService
      .register(requestData)
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Konto utworzone. Za chwile nastapi przekierowanie do logowania.';
          setTimeout(() => this.router.navigate(['/login']), 900);
        },
        error: (err) => {
          this.isLoading = false;
          
          // ZMIANA: Sprawdzamy, czy backend zwrócił obiekt z detalami błędu
          if (err.status === 400 && err.error && err.error.details) {
            let hasSpecificErrors = false;
            const serverErrors = err.error.details; 
            
            // Iterujemy po wszystkich błędach i przypisujemy je do pól w formularzu
            for (const field of Object.keys(serverErrors)) {
              const control = this.registerForm.get(field);
              
              if (control) {
                // Ustawiamy błąd 'serverError' z wiadomością od Spring Boota
                control.setErrors({ serverError: serverErrors[field] });
                control.markAsTouched(); // Wymuszamy pokazanie czerwonej ramki
                hasSpecificErrors = true;
              }
            }

            // Jeśli nie przypisano błędów do konkretnych pól, pokaż ogólny komunikat
            if (!hasSpecificErrors) {
              this.errorMessage = err.error.message || 'Nie udało się założyć konta ze względu na nieprawidłowe dane.';
            }
          } else {
            // Fallback na wypadek innych błędów (np. 500 Internal Server Error)
            this.errorMessage = 'Wystąpił błąd serwera. Spróbuj ponownie później.';
          }
        }
      });
  }
}