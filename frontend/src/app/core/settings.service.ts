import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserSettingsResponse {
  privateAccount: boolean;
  gdprConsent: boolean;
}

@Injectable({ providedIn: 'root' })
export class SettingsService {
  private base = `${environment.apiBaseUrl}/settings`;

  constructor(private http: HttpClient) {}

  getSettings(): Observable<UserSettingsResponse> {
    return this.http.get<UserSettingsResponse>(this.base);
  }

  updateSettings(settings: { privateAccount: boolean }): Observable<UserSettingsResponse> {
    return this.http.put<UserSettingsResponse>(this.base, settings);
  }
}
