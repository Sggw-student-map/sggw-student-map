import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from './review.model';

import { environment } from '../../environments/environment';



@Injectable({ providedIn: 'root' })
export class ReviewsService {
  private apiUrl = environment.apiBaseUrl;

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

  addReview(review: Review, image?: File): Observable<Review> {
    const formData = new FormData();
    formData.append('review', new Blob([JSON.stringify(review)], { type: 'application/json' }));
    if (image) {
      formData.append('image', image);
    }
    return this.http.post<Review>(
      `${this.apiUrl}/places/${review.place_id}/reviews`,
      formData
    );
  }
}