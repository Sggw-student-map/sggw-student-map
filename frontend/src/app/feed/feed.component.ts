import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

import {
  FeedService,
  FeedPost,
  FeedComment,
  CreatePostRequest
} from '../core/feed.service';
import { PlaceService, PlacePin } from '../core/place.service';

export interface PostVM {
  id: number;
  authorName: string;
  initials: string;
  avatarBg: string;
  avatarColor: string;
  timeAgo: string;
  placeName: string;
  placeId: number | null;
  content: string;
  imageUrl?: string;
  likes: number;
  comments: number;
  userLiked: boolean;
  authoredByMe: boolean;
  /** Prywatny profil autora — treść dostępna tylko dla znajomych. */
  authorPrivateAccount: boolean;
  newComment: string;
  commentsOpen: boolean;
  commentsLoading: boolean;
  commentList: CommentVM[];
}

export interface CommentVM {
  id: number;
  authorName: string;
  initials: string;
  avatarBg: string;
  avatarColor: string;
  timeAgo: string;
  content: string;
  authoredByMe: boolean;
}

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  posts: PostVM[] = [];
  places: PlacePin[] = [];

  loading = false;
  errorMessage = '';

  showModal = false;
  newPostContent = '';
  newPostPlaceId: number | null = null;
  selectedImagePreview = '';
  submitting = false;

  private static readonly AVATAR_PALETTE: ReadonlyArray<{ bg: string; color: string }> = [
    { bg: '#E6F1FB', color: '#0C447C' },
    { bg: '#FAEEDA', color: '#633806' },
    { bg: '#E1F5EE', color: '#085041' },
    { bg: '#FBEAF0', color: '#72243E' },
    { bg: '#EEEDFE', color: '#3C3489' },
    { bg: '#FEF3C7', color: '#78350F' }
  ];

  constructor(
    private readonly feedService: FeedService,
    private readonly placeService: PlaceService,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadPosts();
    this.loadPlaces();

    this.route.queryParams.subscribe(params => {
      if (params['openNewPost'] === 'true') {
        this.showModal = true;
      }
      if (params['placeId']) {
        // Przypisanie ID miejsca do selecta w modalu
        this.newPostPlaceId = Number(params['placeId']);
      }
      });
  }

  loadPosts(): void {
    this.loading = true;
    this.errorMessage = '';
    this.feedService.getFeed().subscribe({
      next: (data) => {
        this.posts = data.map((p) => this.toPostVm(p));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Nie udało się załadować postów.';
        this.loading = false;
      }
    });
  }

  loadPlaces(): void {
    this.placeService.getAll().subscribe({
      next: (data) => (this.places = data),
      error: () => (this.places = [])
    });
  }

  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.selectedImagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  toggleLike(post: PostVM): void {
    const previousLiked = post.userLiked;
    post.userLiked = !previousLiked;
    post.likes += post.userLiked ? 1 : -1;

    const request$ = post.userLiked
      ? this.feedService.like(post.id)
      : this.feedService.unlike(post.id);

    request$.subscribe({
      next: (updated) => {
        post.likes = updated.likesCount;
        post.userLiked = updated.likedByMe;
        post.authorPrivateAccount = !!updated.authorPrivateAccount;
      },
      error: () => {
        post.userLiked = previousLiked;
        post.likes += previousLiked ? 1 : -1;
      }
    });
  }

  toggleComments(post: PostVM): void {
    post.commentsOpen = !post.commentsOpen;
    if (post.commentsOpen && post.commentList.length === 0) {
      this.reloadComments(post);
    }
  }

  postComment(post: PostVM): void {
    const text = post.newComment.trim();
    if (!text) return;

    this.feedService.addComment(post.id, { content: text }).subscribe({
      next: (created) => {
        post.commentList.push(this.toCommentVm(created));
        post.comments += 1;
        post.newComment = '';
        post.commentsOpen = true;
      }
    });
  }

  deleteComment(post: PostVM, comment: CommentVM): void {
    this.feedService.deleteComment(comment.id).subscribe({
      next: () => {
        post.commentList = post.commentList.filter((c) => c.id !== comment.id);
        post.comments = Math.max(0, post.comments - 1);
      }
    });
  }

  submitPost(): void {
    const content = this.newPostContent.trim();
    if (!content || this.submitting) return;
    this.submitting = true;

    const body: CreatePostRequest = {
      content,
      placeId: this.newPostPlaceId ?? null,
      imageUrl: this.selectedImagePreview || null
    };

    this.feedService.createPost(body).subscribe({
      next: (created) => {
        this.posts.unshift(this.toPostVm(created));
        this.closeModal();
        this.submitting = false;
      },
      error: () => {
        this.submitting = false;
      }
    });
  }

  deletePost(post: PostVM): void {
    if (!post.authoredByMe) return;
    this.feedService.deletePost(post.id).subscribe({
      next: () => {
        this.posts = this.posts.filter((p) => p.id !== post.id);
      }
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.newPostContent = '';
    this.newPostPlaceId = null;
    this.selectedImagePreview = '';
  }

  trackById(_: number, post: PostVM): number {
    return post.id;
  }

  trackCommentById(_: number, comment: CommentVM): number {
    return comment.id;
  }

  // ---------- mapping helpers ----------

  private reloadComments(post: PostVM): void {
    post.commentsLoading = true;
    this.feedService.getComments(post.id).subscribe({
      next: (data) => {
        post.commentList = data.map((c) => this.toCommentVm(c));
        post.comments = post.commentList.length;
        post.commentsLoading = false;
      },
      error: () => {
        post.commentsLoading = false;
      }
    });
  }

  private toPostVm(p: FeedPost): PostVM {
    const authorName = this.fullName(p.author.firstName, p.author.lastName, p.author.username);
    const palette = this.pickPalette(p.author.id);

    return {
      id: p.id,
      authorName,
      initials: this.initials(authorName),
      avatarBg: palette.bg,
      avatarColor: palette.color,
      timeAgo: this.formatTimeAgo(p.createdAt),
      placeName: p.place?.name ?? 'Bez miejsca',
      placeId: p.place?.id ?? null,
      content: p.content,
      imageUrl: p.imageUrl ?? undefined,
      likes: p.likesCount,
      comments: p.commentsCount,
      userLiked: p.likedByMe,
      authoredByMe: p.authoredByMe,
      authorPrivateAccount: !!p.authorPrivateAccount,
      newComment: '',
      commentsOpen: false,
      commentsLoading: false,
      commentList: []
    };
  }

  private toCommentVm(c: FeedComment): CommentVM {
    const authorName = this.fullName(c.author.firstName, c.author.lastName, c.author.username);
    const palette = this.pickPalette(c.author.id);
    return {
      id: c.id,
      authorName,
      initials: this.initials(authorName),
      avatarBg: palette.bg,
      avatarColor: palette.color,
      timeAgo: this.formatTimeAgo(c.createdAt),
      content: c.content,
      authoredByMe: c.authoredByMe
    };
  }

  private fullName(first: string | null, last: string | null, username: string): string {
    const name = [first, last].filter(Boolean).join(' ').trim();
    return name || username || 'Użytkownik';
  }

  private initials(name: string): string {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return '?';
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  private pickPalette(authorId: number): { bg: string; color: string } {
    const palette = FeedComponent.AVATAR_PALETTE;
    const idx = Math.abs(authorId) % palette.length;
    return palette[idx];
  }

  private formatTimeAgo(isoDate: string): string {
    if (!isoDate) return '';
    const then = new Date(isoDate).getTime();
    if (Number.isNaN(then)) return '';
    const diff = Math.max(0, Date.now() - then);
    const minutes = Math.floor(diff / 60_000);
    if (minutes < 1) return 'przed chwilą';
    if (minutes < 60) return `${minutes} min temu`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours} godz. temu`;
    const days = Math.floor(hours / 24);
    if (days < 7) return `${days} dni temu`;
    return new Date(isoDate).toLocaleDateString('pl-PL');
  }
}
