import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PlacePin {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  description?: string;
  averageRating?: number | null;
}

export interface CreatePlaceRequest {
  name: string;
  latitude: number;
  longitude: number;
  description?: string;
}

export interface NavigationResponse {
  placeId: number;
  placeName: string;
  latitude: number;
  longitude: number;
  googleMapsUrl: string;
}

@Injectable({
  providedIn: 'root',
})
export class PlaceService {
  private readonly url = `${environment.apiBaseUrl}/places`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<PlacePin[]> {
    return this.http.get<PlacePin[]>(this.url);
  }

  search(query: string): Observable<PlacePin[]> {
    return this.http.get<PlacePin[]>(`${this.url}/search`, { params: { q: query } });
  }

  getById(id: number): Observable<PlacePin> {
    return this.http.get<PlacePin>(`${this.url}/${id}`);
  }

  create(request: CreatePlaceRequest): Observable<PlacePin> {
    return this.http.post<PlacePin>(this.url, request);
  }

  update(id: number, request: CreatePlaceRequest): Observable<PlacePin> {
    return this.http.put<PlacePin>(`${this.url}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  getNavigation(id: number): Observable<NavigationResponse> {
    return this.http.get<NavigationResponse>(`${this.url}/${id}/navigation`);
  }
}
