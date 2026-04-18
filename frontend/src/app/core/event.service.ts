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
  likesCount: number;
  likedByMe: boolean;
  interestedCount: number;
  interestedByMe: boolean;
  commentsCount: number;
}

export interface CreateEventRequest {
  nameOfEvent: string;
  idPlace: number;
  dateOfEvent: string;
  comment?: string;
}

export interface CommentResponse {
  id: number;
  content: string;
  createdAt: string;
  authorId: number;
  authorFirstName: string;
  authorLastName: string;
  isMyComment: boolean;
  showMenu?: boolean;
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

  likeEvent(eventId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/${eventId}/like`, {});
  }

  unlikeEvent(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${eventId}/like`);
  }

  markInterested(eventId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/${eventId}/interested`, {});
  }

  unmarkInterested(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${eventId}/interested`);
  }

  getComments(eventId: number): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.base}/${eventId}/comments`);
  }

  addComment(eventId: number, content: string): Observable<CommentResponse> {
    return this.http.post<CommentResponse>(`${this.base}/${eventId}/comments`, { content });
  }

  getPlaces(): Observable<PlaceOption[]> {
    return this.http.get<PlaceOption[]>(this.placesBase);
  }

  deleteEvent(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${eventId}`);
  }

  deleteComment(eventId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${eventId}/comments/${commentId}`);
  }
}
