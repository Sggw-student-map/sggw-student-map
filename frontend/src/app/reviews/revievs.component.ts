import { Component, Input, OnInit } from '@angular/core';
import { ReviewsService } from './reviews.service';
import { Review } from './review.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],  
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.css']
})
export class ReviewsComponent implements OnInit {
  @Input() placeId!: number;

  reviews: Review[] = [];
  newRating = 0;
  newComment = '';
  hoveredStar = 0;
  isSubmitting = false;
  averageRating = 0;

  constructor(private reviewsService: ReviewsService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    this.reviewsService.getReviewsByPlace(this.placeId).subscribe(data => {
      this.reviews = data;
      this.averageRating = data.length
        ? data.reduce((sum, r) => sum + r.rating, 0) / data.length
        : 0;
    });
  }

  setRating(star: number): void {
    this.newRating = star;
  }

  submitReview(): void {
    if (!this.newRating || !this.newComment.trim()) return;
    this.isSubmitting = true;

    const review: Review = {
      place_id: this.placeId,
      rating: this.newRating,
      comment: this.newComment
    };

    this.reviewsService.addReview(review).subscribe({
      next: () => {
        this.newRating = 0;
        this.newComment = '';
        this.isSubmitting = false;
        this.loadReviews();
      },
      error: () => { this.isSubmitting = false; }
    });
  }

  getStars(rating: number): string[] {
    return Array(5).fill('').map((_, i) =>
      i < Math.round(rating) ? '★' : '☆'
    );
  }
}