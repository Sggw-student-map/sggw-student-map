import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type PlaceSortOption = 'RECENT' | 'HIGHEST_RATED' | 'LOWEST_RATED';

export interface PlaceQueryOptions {
  sort?: PlaceSortOption;
  minRating?: number;
  onlyRated?: boolean;
  limit?: number | null;
}

export interface PlacePin {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  description?: string;
  imageUrl?: string | null;
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

  create(request: CreatePlaceRequest): Observable<any> {
    return this.http.post<any>(this.url, request);
  }

  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.url}/${id}`);
  }

  update(id: number, request: CreatePlaceRequest): Observable<PlacePin> {
    return this.http.put<PlacePin>(`${this.url}/${id}`, request);
  }

  getNavigation(id: number): Observable<NavigationResponse> {
    return this.http.get<NavigationResponse>(`${this.url}/${id}/navigation`);
  }

  getPending(): Observable<any[]> {
    return this.http.get<any[]>(`${this.url}/pending`);
  }

  approve(id: number): Observable<any> {
    return this.http.post<any>(`${this.url}/pending/${id}/approve`, {});
  }

  reject(id: number): Observable<any> {
    return this.http.post<any>(`${this.url}/pending/${id}/reject`, {});
  }

  uploadImage(placeId: number, image: File): Observable<PlacePin> {
    const formData = new FormData();
    formData.append('image', image);
    return this.http.post<PlacePin>(`${this.url}/${placeId}/image`, formData);
  }
}
