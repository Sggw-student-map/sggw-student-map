import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { AuthService } from '../core/auth.service';

import kampus1Img from '../../../resources/images/kampus1.jpg';
import kampus2Img from '../../../resources/images/kampus2.jpg';
import wydarzenia1Img from '../../../resources/images/wydarzenia1.png';
import spolecznosc1Img from '../../../resources/images/spolecznosc1.png';

interface CampusSlide {
  src: string;
  title: string;
  subtitle: string;
  gradient: string;
  imageOk: boolean;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register implements OnInit, OnDestroy {
  registerForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isRegistrationSuccessful = false;

  readonly slides: CampusSlide[] = [
    {
      src: kampus1Img,
      title: 'Kampus SGGW',
      subtitle: 'Twoje miejsce na mapie studenckiego życia',
      gradient: 'linear-gradient(135deg, #134e4a 0%, #0f766e 45%, #10b981 100%)',
      imageOk: true,
    },
    {
      src: kampus2Img,
      title: 'Poznaj kampus',
      subtitle: 'Odkryj budynki, akademiki i ulubione zakątki studentów',
      gradient: 'linear-gradient(135deg, #1e3a8a 0%, #2563eb 50%, #06b6d4 100%)',
      imageOk: true,
    },
    {
      src: wydarzenia1Img,
      title: 'Wydarzenia studenckie',
      subtitle: 'Bądź na bieżąco z tym, co dzieje się w okolicy',
      gradient: 'linear-gradient(135deg, #7c2d12 0%, #ea580c 45%, #f59e0b 100%)',
      imageOk: true,
    },
    {
      src: spolecznosc1Img,
      title: 'Społeczność SGGW',
      subtitle: 'Spotykaj się, dziel opiniami i twórz wspomnienia',
      gradient: 'linear-gradient(135deg, #4c1d95 0%, #7c3aed 50%, #ec4899 100%)',
      imageOk: true,
    },
  ];

  currentSlide = 0;
  private slideTimer: ReturnType<typeof setInterval> | null = null;
  private readonly slideIntervalMs = 6000;

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

  ngOnInit(): void {
    this.startSlideShow();
  }

  ngOnDestroy(): void {
    this.stopSlideShow();
  }

  goToSlide(index: number): void {
    if (index === this.currentSlide) return;
    this.currentSlide = (index + this.slides.length) % this.slides.length;
    this.restartSlideShow();
  }

  nextSlide(): void {
    this.goToSlide(this.currentSlide + 1);
  }

  prevSlide(): void {
    this.goToSlide(this.currentSlide - 1);
  }

  onImageError(slide: CampusSlide): void {
    slide.imageOk = false;
  }

  private startSlideShow(): void {
    this.stopSlideShow();
    this.slideTimer = setInterval(() => {
      this.currentSlide = (this.currentSlide + 1) % this.slides.length;
    }, this.slideIntervalMs);
  }

  private stopSlideShow(): void {
    if (this.slideTimer) {
      clearInterval(this.slideTimer);
      this.slideTimer = null;
    }
  }

  private restartSlideShow(): void {
    this.startSlideShow();
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
          this.isRegistrationSuccessful = true;
          this.successMessage = 'Konto zostało pomyślnie utworzone! Sprawdź swoją skrzynkę e-mail, aby potwierdzić rejestrację.';
        },
        error: (err) => {
          this.isLoading = false;

          if (err.status === 400) {
            this.errorMessage = 'Wprowadzone dane są niepoprawne.';
          } else {
            this.errorMessage = 'Wystąpił błąd serwera. Spróbuj ponownie później.';
          }
        }
      });
  }
  goToLogin(): void {
    this.router.navigate(['/login']);
  }
  
}
