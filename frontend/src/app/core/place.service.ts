import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PlacePin {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
}

@Injectable({
  providedIn: 'root'
})
export class PlaceService {
  constructor(private readonly http: HttpClient) {}

  getPins(): Observable<PlacePin[]> {
    return this.http.get<PlacePin[]>(`${environment.apiBaseUrl}/pins`);
  }
}
