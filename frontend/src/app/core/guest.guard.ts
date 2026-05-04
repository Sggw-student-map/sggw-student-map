import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenStorageService } from './token-storage.service';

export const guestGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);

  if (!tokenStorage.getAccessToken()) {
    return true;
  }

  return router.createUrlTree(['/map']);
};
