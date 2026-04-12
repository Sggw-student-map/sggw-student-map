import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ReviewsService } from './reviews.service';
import { Review } from './review.model';

export interface ReviewVM {
  id: number;
  author: string;
  initials: string;
  avatarBg: string;
  avatarColor: string;
  time: string;
  rating: number;
  placeName: string;
  placeId: number;
  placeImage?: string;
  comment: string;
}

export interface Place {
  id: number;
  name: string;
}

@Component({
  selector: 'app-opinions-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './opinions-page.component.html',
  styleUrls: ['./opinions-page.component.css']
})
export class OpinionsPageComponent implements OnInit {
  reviews: ReviewVM[] = [];
  showModal = false;
  modalRating = 0;
  modalComment = '';
  modalPlaceId: number | null = null;
  hovered = 0;

  places: Place[] = [];

  constructor(private reviewsService: ReviewsService, private http: HttpClient) {}

  ngOnInit(): void {
    this.loadReviews();
    this.loadPlaces();
  }

  loadReviews(): void {
    this.reviewsService.getAllReviews().subscribe((data: Review[]) => {
      this.reviews = data.map(r => {
        const authorName = r.author || 'Anonim';
        const initials = authorName.substring(0, 2).toUpperCase();
        const time = r.created_at ? new Date(r.created_at).toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' }) : '';
        return {
          id: r.id || 0,
          author: authorName,
          initials: initials,
          avatarBg: '#E6F1FB',
          avatarColor: '#0C447C',
          time: time,
          rating: r.rating,
          placeName: 'Miejsce ' + r.place_id,
          placeId: r.place_id,
          comment: r.comment
        };
      });
    });
  }

  loadPlaces(): void {
    this.http.get<Place[]>('/api/places').subscribe(data => {
      this.places = data;
    });
  }

  getPlaceName(placeId: number): string {
    const place = this.places.find(p => p.id === placeId);
    return place ? place.name : 'Miejsce ' + placeId;
  }

  starsArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, i) => i < rating);
  }

  openModal(): void { this.showModal = true; }

  closeModal(): void {
    this.showModal = false;
    this.modalRating = 0;
    this.modalComment = '';
    this.modalPlaceId = null;
    this.hovered = 0;
  }

  submitReview(): void {
    if (!this.modalRating || !this.modalComment.trim() || !this.modalPlaceId) return;

    const review: Review = {
      place_id: this.modalPlaceId,
      rating: this.modalRating,
      comment: this.modalComment
    };

    this.reviewsService.addReview(review).subscribe(() => {
      this.loadReviews();
      this.closeModal();
    });
  }
}