import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

import { environment } from '../../environments/environment';


export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  username: string;
  password: string;
  email: string;
}

export interface JwtResponse {
  token: string;
}

export interface CurrentUser {
  id: number;
  firstName: string | null;
  lastName: string | null;
  username: string;
  email: string;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private readonly http: HttpClient,
    private readonly tokenStorage: TokenStorageService
  ) {}

  login(payload: LoginRequest): Observable<JwtResponse> {
    return this.http
      .post<JwtResponse>(`${environment.authBaseUrl}/login`, payload, { withCredentials: true })
      .pipe(tap((response) => this.tokenStorage.setAccessToken(response.token)));
  }

  register(payload: RegisterRequest): Observable<unknown> {
    return this.http.post(`${environment.baseUrl}/users`, payload);
  }

  refresh(): Observable<JwtResponse> {
    return this.http
      .post<JwtResponse>(`${environment.authBaseUrl}/refresh`, {}, { withCredentials: true })
      .pipe(tap((response) => this.tokenStorage.setAccessToken(response.token)));
  }

  me(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${environment.authBaseUrl}/me`);
  }

  logout(): void {
    this.tokenStorage.clear();
  }
}
