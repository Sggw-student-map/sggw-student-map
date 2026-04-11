import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from './review.model';

@Injectable({ providedIn: 'root' })
export class ReviewsService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getReviewsByPlace(placeId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/places/${placeId}/reviews`);
  }

  getAllReviews(): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/reviews`);
}

  addReview(review: Review): Observable<Review> {
    return this.http.post<Review>(
      `${this.apiUrl}/places/${review.place_id}/reviews`,
      review
    );
  }
}