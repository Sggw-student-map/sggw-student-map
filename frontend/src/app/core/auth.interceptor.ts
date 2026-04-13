import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenStorageService } from './token-storage.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const token = tokenStorage.getAccessToken();

  console.log(`[Auth Interceptor] Requesting: ${req.url}`);
  console.log(`[Auth Interceptor] Current Token:`, token);

  if (!token) {
    console.warn(`[Auth Interceptor] No token found, sending request without Authorization header.`);
    return next(req);
  }

  const authorizedReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  console.log(`[Auth Interceptor] Successfully attached Bearer token.`);
  return next(authorizedReq);
};
