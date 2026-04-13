import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
// import { PostService } from '../core/post.service'; // ← odkomentuj gdy API gotowe

export interface PostVM {
  id: number;
  authorName: string;
  initials: string;
  avatarBg: string;
  avatarColor: string;
  timeAgo: string;
  placeName: string;
  placeId: number;
  content: string;
  imageUrl?: string;
  likes: number;
  comments: number;
  userLiked: boolean;
  newComment: string;
}

// ─── MOCK DATA ── usuń ten blok gdy API gotowe ───────────────────────────────
const MOCK_POSTS: PostVM[] = [
  {
    id: 1,
    authorName: 'Anna Kowalska',
    initials: 'AK',
    avatarBg: '#E6F1FB',
    avatarColor: '#0C447C',
    timeAgo: '12 min temu',
    placeName: 'Biblioteka Główna',
    placeId: 1,
    content: 'Niesamowity widok z biblioteki dziś rano! Polecam zajść po mapę kampusu.',
    likes: 8,
    comments: 2,
    userLiked: false,
    newComment: ''
  },
  {
    id: 2,
    authorName: 'Michał Brzezik',
    initials: 'MB',
    avatarBg: '#FAEEDA',
    avatarColor: '#633806',
    timeAgo: '1 godz. temu',
    placeName: 'Bar Studencki',
    placeId: 2,
    content: 'Właśnie próbowałem nowe kanapki w barze – mocno polecam wersję z awokado! Kolejka nie była długa, obsługa miła.',
    likes: 14,
    comments: 5,
    userLiked: true,
    newComment: ''
  },
  {
    id: 3,
    authorName: 'Piotr Wierzbicki',
    initials: 'PW',
    avatarBg: '#E1F5EE',
    avatarColor: '#085041',
    timeAgo: '2 godz. temu',
    placeName: 'Fontanna SGGW',
    placeId: 3,
    content: 'Dołączyłem do jutrzejszego joggingu po kampusie — ktoś jeszcze? Startujemy o 7:00.',
    likes: 3,
    comments: 7,
    userLiked: false,
    newComment: ''
  },
  {
    id: 4,
    authorName: 'Julia Stankiewicz',
    initials: 'JS',
    avatarBg: '#FBEAF0',
    avatarColor: '#72243E',
    timeAgo: '3 godz. temu',
    placeName: 'Stołówka DS-2',
    placeId: 4,
    content: 'Stołówka działa teraz do 19:00. Miałam nadzieję na późniejszy obiad, niestety...',
    likes: 2,
    comments: 4,
    userLiked: false,
    newComment: ''
  }
];
// ────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  posts: PostVM[] = [];
  showModal = false;
  newPostContent = '';
  submitting = false;
  selectedImagePreview = '';

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.selectedImagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }
  // constructor(private postService: PostService) {} // ← odkomentuj gdy API gotowe
  constructor() {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    // ── MOCK ── usuń i zamień gdy API gotowe ─────────────────────────────────
    this.posts = MOCK_POSTS.map(p => ({ ...p }));

    // ── API ── odkomentuj gdy API gotowe ─────────────────────────────────────
    // this.postService.getAllPosts().subscribe(data => {
    //   this.posts = data.map(p => ({
    //     ...p,
    //     initials: p.authorFirstName[0] + p.authorLastName[0],
    //     avatarBg: '#E6F1FB',
    //     avatarColor: '#0C447C',
    //     timeAgo: this.formatTime(p.createdAt),
    //     userLiked: p.likedByMe,
    //     likes: p.likesCount,
    //     newComment: ''
    //   }));
    // });
  }

  toggleLike(post: PostVM): void {
    post.userLiked = !post.userLiked;
    post.likes += post.userLiked ? 1 : -1;
    // ── API ── odkomentuj gdy API gotowe ─────────────────────────────────────
    // if (post.userLiked) this.postService.likePost(post.id).subscribe();
    // else this.postService.unlikePost(post.id).subscribe();
  }

  postComment(post: PostVM): void {
    if (!post.newComment.trim()) return;
    post.comments += 1;
    post.newComment = '';
    // ── API ── this.postService.addComment(post.id, comment).subscribe();
  }

  submitPost(): void {
    if (!this.newPostContent.trim() || this.submitting) return;
    this.submitting = true;


    // ── MOCK ── usuń gdy API gotowe ──────────────────────────────────────────
    const newPost: PostVM = {
      id: Date.now(),
      authorName: 'Ty',
      initials: 'TY',
      avatarBg: '#EEEDFE',
      avatarColor: '#3C3489',
      timeAgo: 'przed chwilą',
      placeName: 'Nieznane miejsce',
      placeId: 0,
      content: this.newPostContent,
      likes: 0,
      comments: 0,
      userLiked: false,
      newComment: '',
      imageUrl: this.selectedImagePreview || undefined,
    };
    this.posts.unshift(newPost);
    this.closeModal();
    this.submitting = false;

    // ── API ── odkomentuj gdy API gotowe ─────────────────────────────────────
    // this.postService.createPost({ idPlace: selectedPlaceId, content: this.newPostContent })
    //   .subscribe({
    //     next: (p) => {
    //       this.posts.unshift({ ...p, initials: ..., avatarBg: ..., userLiked: false, newComment: '' });
    //       this.closeModal();
    //       this.submitting = false;
    //     },
    //     error: () => { this.submitting = false; }
    //   });
  }

  closeModal(): void {
    this.showModal = false;
    this.newPostContent = '';
    this.selectedImagePreview = '';
  }

  trackById(_: number, post: PostVM): number {
    return post.id;
  }
}
