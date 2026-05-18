import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type PlaceSortOption = 'RECENT' | 'HIGHEST_RATED' | 'LOWEST_RATED';

export interface PlaceQueryOptions {
  sort?: PlaceSortOption;
  minRating?: number;
  maxRating?: number;
  onlyRated?: boolean;
  limit?: number | null;
}

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

  getAll(options: PlaceQueryOptions = {}): Observable<PlacePin[]> {
    let params = new HttpParams();
    if (options.sort) {
      params = params.set('sort', options.sort);
    }
    if (options.minRating != null && options.minRating > 0) {
      params = params.set('minRating', options.minRating.toString());
    }
    if (options.maxRating != null && options.maxRating > 0 && options.maxRating < 5) {
      params = params.set('maxRating', options.maxRating.toString());
    }
    if (options.onlyRated) {
      params = params.set('onlyRated', 'true');
    }
    if (options.limit != null && options.limit > 0) {
      params = params.set('limit', options.limit.toString());
    }
    return this.http.get<PlacePin[]>(this.url, { params });
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
