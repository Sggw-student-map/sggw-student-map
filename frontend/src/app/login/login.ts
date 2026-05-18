import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { switchMap } from 'rxjs/operators';
import { AuthService } from '../core/auth.service';
import { UserStateService } from '../core/user-state.service';

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
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit, OnDestroy {
  loginForm!: FormGroup;
  isLoading = false;
  errorMessage = '';

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
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly userState: UserStateService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.startSlideShow();
  }

  ngOnDestroy(): void {
    this.stopSlideShow();
  }

  goToSlide(index: number): void {
    if (index === this.currentSlide) {
      return;
    }
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

  onLogin(): void {
    this.errorMessage = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService
      .login(this.loginForm.value)
      .pipe(switchMap(() => this.userState.loadUser()))
      .subscribe({
        next: () => {
          this.isLoading = false;
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
          this.router.navigateByUrl(returnUrl && returnUrl.startsWith('/') ? returnUrl : '/map');
        },
        error: () => {
          this.isLoading = false;
          this.errorMessage = 'Niepoprawne dane logowania albo niedostępny backend.';
        },
      });
  }
}
