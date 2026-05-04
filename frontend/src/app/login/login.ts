import { Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { switchMap } from 'rxjs/operators';
import { AuthService } from '../core/auth.service';
import { UserStateService } from '../core/user-state.service';

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
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly userState: UserStateService
  ) {}

  onLogin(username: string, password: string): void {
    this.errorMessage = '';

    if (!username.trim() || !password.trim()) {
      this.errorMessage = 'Wpisz login i hasło.';
      return;
    }

    this.isLoading = true;
    this.authService
      .login({ username: username.trim(), password: password.trim() })
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
