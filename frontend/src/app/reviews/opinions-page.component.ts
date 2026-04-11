// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ReviewsService } from './reviews.service';

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
// import { ReviewsService } from './reviews.service'; // ← uncomment when API ready

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

// ─── MOCK DATA ── remove this block when API is ready ───────────────────────
const MOCK_REVIEWS: ReviewVM[] = [
  {
    id: 1,
    author: 'Witalij Filipienko',
    initials: 'WF',
    avatarBg: '#E6F1FB',
    avatarColor: '#0C447C',
    time: '12:11',
    rating: 2,
    placeName: 'Wydział Zastosowań Informatyki i Matematyki',
    comment: 'jak mnie przepuszcza za 24 ectami to podwyższe ocene narazie uważam za dramat, na plus tylko przyciski w windzie',
    likes: 27,
    dislikes: 1,
    userLiked: false,
    newComment: ''
  },
  {
    id: 2,
    author: 'Adriano Żyskowski',
    initials: 'AZ',
    avatarBg: '#E1F5EE',
    avatarColor: '#085041',
    time: '9:58',
    rating: 3,
    placeName: 'Pomnik przyrody "Topole"',
    comment: 'dobre miejsce na piłkę ale powierzchnia za krzywa żeby grać we flanki, ja bym tu przejechał jakimś traktorem albo zamontował krawężniki',
    likes: 11,
    dislikes: 0,
    userLiked: false,
    newComment: ''
  },
  {
    id: 3,
    author: 'Stanisław Ostatkiewicz',
    initials: 'SO',
    avatarBg: '#FAEEDA',
    avatarColor: '#633806',
    time: '9:11',
    rating: 2,
    placeName: 'Boisko do gry',
    comment: 'Niskiej jakości beton i jakieś denerwujące barierki dookoła.',
    likes: 14,
    dislikes: 0,
    userLiked: false,
    newComment: ''
  },
  {
    id: 4,
    author: 'Bartosz Adamowicz',
    initials: 'BA',
    avatarBg: '#EEEDFE',
    avatarColor: '#3C3489',
    time: '12:16',
    rating: 4,
    placeName: 'Wydział Zastosowań Informatyki i Matematyki',
    comment: 'rol',
    likes: 3,
    dislikes: 0,
    userLiked: false,
    newComment: ''
  }
];
// ────────────────────────────────────────────────────────────────────────────

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
  hovered = 0;

  // constructor(private reviewsService: ReviewsService) {} // ← uncomment when API ready
  constructor() {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    // ── MOCK ── swap these two blocks when API is ready ──────────────────────
    this.reviews = MOCK_REVIEWS.map(r => ({ ...r }));

    // ── API ── uncomment this block and delete the mock line above ───────────
    // this.reviewsService.getAllReviews().subscribe(data => {
    //   this.reviews = data.map(r => ({
    //     ...r,
    //     userLiked: false,
    //     newComment: ''
    //   }));
    // });
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
    r.newComment = '';
  }

  openModal(): void { this.showModal = true; }

  closeModal(): void {
    this.showModal = false;
    this.modalRating = 0;
    this.modalComment = '';
    this.hovered = 0;
  }

  submitReview(): void {
    if (!this.modalRating || !this.modalComment.trim()) return;

    // ── MOCK ── adds card locally, delete when API is ready ──────────────────
    const newReview: ReviewVM = {
      id: Date.now(),
      author: 'Ty',
      initials: 'TY',
      avatarBg: '#EEEDFE',
      avatarColor: '#3C3489',
      time: new Date().toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' }),
      rating: this.modalRating,
      placeName: 'Nowe miejsce',
      comment: this.modalComment,
      likes: 0,
      dislikes: 0,
      userLiked: false,
      newComment: ''
    };
    this.reviews.unshift(newReview);

    // ── API ── uncomment this block and delete the mock block above ──────────
    // this.reviewsService.addReview({
    //   place_id: 1,
    //   rating: this.modalRating,
    //   comment: this.modalComment
    // }).subscribe(() => this.loadReviews());

    this.closeModal();
  }
}




// export interface ReviewVM {
//   id: number;
//   author: string;
//   initials: string;
//   avatarBg: string;
//   avatarColor: string;
//   time: string;
//   rating: number;
//   placeName: string;
//   placeImage?: string;
//   comment: string;
//   likes: number;
//   dislikes: number;
//   userLiked: boolean;
//   newComment: string;
// }

// @Component({
//   selector: 'app-opinions-page',
//   standalone: true,
//   imports: [CommonModule, FormsModule],  
//   templateUrl: './opinions-page.component.html',
//   styleUrls: ['./opinions-page.component.css']
// })

// // @Component({
// //   selector: 'app-opinions-page',
// //   templateUrl: './opinions-page.component.html',
// //   styleUrls: ['./opinions-page.component.css']
// // })
// export class OpinionsPageComponent implements OnInit {
//   reviews: ReviewVM[] = [];
//   showModal = false;
//   modalRating = 0;
//   modalComment = '';
//   hovered = 0;

//   constructor(private reviewsService: ReviewsService) {}

//   ngOnInit(): void {
//     this.reviewsService.getAllReviews().subscribe(data => {
//       this.reviews = data.map(r => ({
//         ...r,
//         userLiked: false,
//         newComment: ''
//       }));
//     });
//   }

//   starsArray(rating: number): boolean[] {
//     return Array(5).fill(false).map((_, i) => i < rating);
//   }

//   toggleLike(r: ReviewVM): void {
//     r.userLiked = !r.userLiked;
//     r.likes += r.userLiked ? 1 : -1;
//   }

//   toggleDislike(r: ReviewVM): void {
//     r.dislikes += 1;
//   }

//   postComment(r: ReviewVM): void {
//     if (!r.newComment.trim()) return;
//     // call CommentsService here
//     r.newComment = '';
//   }

//   openModal(): void { this.showModal = true; }
//   closeModal(): void {
//     this.showModal = false;
//     this.modalRating = 0;
//     this.modalComment = '';
//   }

//   submitReview(): void {
//     if (!this.modalRating || !this.modalComment.trim()) return;
//     this.reviewsService.addReview({
//       place_id: 1, // pass selected place id
//       rating: this.modalRating,
//       comment: this.modalComment
//     }).subscribe(() => {
//       this.closeModal();
//       this.ngOnInit(); // reload
//     });
//   }
// }