import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewsService } from './reviews.service';




export interface ReviewVM {
  id: number;
  author: string;
  initials: string;
  avatarBg: string;
  avatarColor: string;
  time: string;
  rating: number;
  placeName: string;
  placeImage?: string;
  comment: string;
  likes: number;
  dislikes: number;
  userLiked: boolean;
  newComment: string;
}

@Component({
  selector: 'app-opinions-page',
  standalone: true,
  imports: [CommonModule, FormsModule],  
  templateUrl: './opinions-page.component.html',
  styleUrls: ['./opinions-page.component.css']
})

// @Component({
//   selector: 'app-opinions-page',
//   templateUrl: './opinions-page.component.html',
//   styleUrls: ['./opinions-page.component.css']
// })
export class OpinionsPageComponent implements OnInit {
  reviews: ReviewVM[] = [];
  showModal = false;
  modalRating = 0;
  modalComment = '';
  hovered = 0;

  constructor(private reviewsService: ReviewsService) {}

  ngOnInit(): void {
    this.reviewsService.getAllReviews().subscribe(data => {
      this.reviews = data.map(r => ({
        ...r,
        userLiked: false,
        newComment: ''
      }));
    });
  }

  starsArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, i) => i < rating);
  }

  toggleLike(r: ReviewVM): void {
    r.userLiked = !r.userLiked;
    r.likes += r.userLiked ? 1 : -1;
  }

  toggleDislike(r: ReviewVM): void {
    r.dislikes += 1;
  }

  postComment(r: ReviewVM): void {
    if (!r.newComment.trim()) return;
    // call CommentsService here
    r.newComment = '';
  }

  openModal(): void { this.showModal = true; }
  closeModal(): void {
    this.showModal = false;
    this.modalRating = 0;
    this.modalComment = '';
  }

  submitReview(): void {
    if (!this.modalRating || !this.modalComment.trim()) return;
    this.reviewsService.addReview({
      place_id: 1, // pass selected place id
      rating: this.modalRating,
      comment: this.modalComment
    }).subscribe(() => {
      this.closeModal();
      this.ngOnInit(); // reload
    });
  }
}