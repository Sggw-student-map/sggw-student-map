import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PlaceOption {
  id: number;
  name: string;
}

export interface EventResponse {
  id: number;
  nameOfEvent: string;
  dateOfEvent: string;
  comment?: string;
  placeId: number;
  placeName: string;
  // placeImageUrl?: string; // ← odkomentuj gdy places będą miały image_url
  organizerId: number;
  organizerFirstName: string;
  organizerLastName: string;
  participantCount: number;
  joinedByMe: boolean;
  organizedByMe: boolean;
}

export interface CreateEventRequest {
  nameOfEvent: string;
  idPlace: number;
  dateOfEvent: string;
  comment?: string;
}

@Injectable({ providedIn: 'root' })
export class EventService {
  private base = `${environment.apiBaseUrl}/events`;
  private placesBase = `${environment.apiBaseUrl}/places`;

  constructor(private http: HttpClient) {}

  getAllEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(this.base);
  }

  createEvent(request: CreateEventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(this.base, request);
  }

  joinEvent(eventId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/${eventId}/join`, {});
  }

  leaveEvent(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${eventId}/join`);
  }

  getPlaces(): Observable<PlaceOption[]> {
    return this.http.get<PlaceOption[]>(this.placesBase);
  }
}
