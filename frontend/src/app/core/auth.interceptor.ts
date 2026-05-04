import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { TokenStorageService } from './token-storage.service';
import { UserStateService } from './user-state.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  const userState = inject(UserStateService);
  const token = tokenStorage.getAccessToken();

  console.log(`[Auth Interceptor] Requesting: ${req.url}`);
  console.log(`[Auth Interceptor] Current Token:`, token);

  const authorizedReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  if (!token) {
    console.warn(`[Auth Interceptor] No token found, sending request without Authorization header.`);
  } else {
    console.log(`[Auth Interceptor] Successfully attached Bearer token.`);
  }

  return next(authorizedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && token) {
        console.warn('[Auth Interceptor] 401 received, clearing session and redirecting to /login.');
        tokenStorage.clear();
        userState.clear();

        const currentUrl = router.url;
        const onAuthPage =
          currentUrl.startsWith('/login') || currentUrl.startsWith('/register');

        router.navigate(['/login'], {
          queryParams: onAuthPage ? undefined : { returnUrl: currentUrl }
        });
      }
      return throwError(() => error);
    })
  );
};
