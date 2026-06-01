import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ReviewsService } from './reviews.service';
import { Review } from './review.model';

import { environment } from '../../environments/environment';




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
  imageUrl?: string;
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
  myReviewsMode = false;
  selectedImageFile: File | null = null;
  selectedImagePreview = '';
  imageError = '';

  private static readonly MAX_FILE_SIZE = 5 * 1024 * 1024;
  private static readonly ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp'];

  places: Place[] = [];

  constructor(
    private reviewsService: ReviewsService,
    private http: HttpClient,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.myReviewsMode = params.get('mine') === 'true';
      this.loadReviews();
    });
    this.loadPlaces();
  }

  loadReviews(): void {
    const source$ = this.myReviewsMode
      ? this.reviewsService.getMyReviews()
      : this.reviewsService.getAllReviews();

    source$.subscribe((data: Review[]) => {
      const sortedData = data.sort((a, b) => {
        const dateA = a.created_at ? new Date(a.created_at).getTime() : 0;
        const dateB = b.created_at ? new Date(b.created_at).getTime() : 0;
        return dateB - dateA;
      });

      this.reviews = sortedData.map(r => {
        const authorName = r.author || 'Anonim';
        const initials = authorName.substring(0, 2).toUpperCase();
        
        const time = r.created_at 
          ? new Date(r.created_at).toLocaleString('pl-PL', { 
              day: '2-digit', 
              month: '2-digit', 
              year: 'numeric', 
              hour: '2-digit', 
              minute: '2-digit' 
            }) 
          : '';

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
          imageUrl: r.image_url ?? undefined,
          comment: r.comment
        };
      });
    });
  }

  loadPlaces(): void {
    this.http.get<Place[]>(`${environment.apiBaseUrl}/places`).subscribe(data => {
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

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.imageError = '';

    if (!OpinionsPageComponent.ALLOWED_TYPES.includes(file.type)) {
      this.imageError = 'Dozwolone formaty: JPEG, PNG, WebP.';
      return;
    }
    if (file.size > OpinionsPageComponent.MAX_FILE_SIZE) {
      this.imageError = 'Maksymalny rozmiar pliku: 5 MB.';
      return;
    }

    this.selectedImageFile = file;
    this.selectedImagePreview = URL.createObjectURL(file);
  }

  closeModal(): void {
    if (this.selectedImagePreview) {
      URL.revokeObjectURL(this.selectedImagePreview);
    }
    this.showModal = false;
    this.modalRating = 0;
    this.modalComment = '';
    this.modalPlaceId = null;
    this.hovered = 0;
    this.selectedImageFile = null;
    this.selectedImagePreview = '';
    this.imageError = '';
  }

  submitReview(): void {
    if (!this.modalRating || !this.modalComment.trim() || !this.modalPlaceId) return;

    const review: Review = {
      place_id: this.modalPlaceId,
      rating: this.modalRating,
      comment: this.modalComment
    };

    this.reviewsService.addReview(review, this.selectedImageFile ?? undefined).subscribe(() => {
      this.loadReviews();
      this.closeModal();
    });
  }
}