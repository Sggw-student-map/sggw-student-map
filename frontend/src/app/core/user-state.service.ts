import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AuthService, CurrentUser } from './auth.service';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root',
})
export class UserStateService {
  private readonly userSubject = new BehaviorSubject<CurrentUser | null>(null);
  private loaded = false;

  readonly user$: Observable<CurrentUser | null> = this.userSubject.asObservable();

  constructor(
    private readonly authService: AuthService,
    private readonly tokenStorage: TokenStorageService
  ) {}

  get currentUser(): CurrentUser | null {
    return this.userSubject.value;
  }

  get isLoggedIn(): boolean {
    return !!this.tokenStorage.getAccessToken();
  }

  loadUser(): Observable<CurrentUser | null> {
    if (this.loaded && this.userSubject.value) {
      return of(this.userSubject.value);
    }

    if (!this.isLoggedIn) {
      this.userSubject.next(null);
      return of(null);
    }

    return this.authService.me().pipe(
      tap((user) => {
        this.userSubject.next(user);
        this.loaded = true;
      }),
      catchError(() => {
        this.userSubject.next(null);
        this.loaded = false;
        return of(null);
      })
    );
  }

  clear(): void {
    this.userSubject.next(null);
    this.loaded = false;
  }

  getInitials(user: CurrentUser | null): string {
    if (!user) return '?';
    const first = user.firstName?.charAt(0) ?? '';
    const last = user.lastName?.charAt(0) ?? '';
    if (first || last) return (first + last).toUpperCase();
    return user.username.charAt(0).toUpperCase();
  }

  getDisplayName(user: CurrentUser | null): string {
    if (!user) return 'Użytkownik';
    if (user.firstName) {
      return user.lastName ? `${user.firstName} ${user.lastName}` : user.firstName;
    }
    return user.username;
  }
}
