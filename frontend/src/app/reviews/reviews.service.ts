import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from './review.model';

@Injectable({ providedIn: 'root' })
export class ReviewsService {
  private apiUrl = '/api'; // Using relative path avoids CORS preflight 401 issues

  constructor(private http: HttpClient) {}

  getReviewsByPlace(placeId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/places/${placeId}/reviews`);
  }

  getAllReviews(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/reviews`);
  }

  getMyReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/reviews/my`);
  }

  addReview(review: Review): Observable<Review> {
    return this.http.post<Review>(
      `${this.apiUrl}/places/${review.place_id}/reviews`,
      review
    );
  }
}