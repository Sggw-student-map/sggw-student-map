import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { PostService, PostResponse, PostCommentResponse } from '../core/post.service';
import { environment } from '../../environments/environment';

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
  // imageUrl?: string; // ← odkomentuj gdy chmura gotowa
  likes: number;
  comments: number;
  userLiked: boolean;
  newComment: string;
  showComments: boolean;
  commentsList: PostCommentResponse[];
  isMyPost: boolean;
  showMenu: boolean;
}

const AVATAR_COLORS = [
  { bg: '#E6F1FB', fg: '#0C447C' },
  { bg: '#FAEEDA', fg: '#633806' },
  { bg: '#E1F5EE', fg: '#085041' },
  { bg: '#FBEAF0', fg: '#72243E' },
  { bg: '#EEEDFE', fg: '#3C3489' },
];

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  posts: PostVM[] = [];
  places: { id: number; name: string }[] = [];
  showModal = false;
  newPostContent = '';
  selectedPlaceId: number | null = null;
  submitting = false;
  // selectedImagePreview = ''; // ← odkomentuj gdy chmura gotowa

  constructor(private postService: PostService, private http: HttpClient) {}

  ngOnInit(): void {
    this.loadPosts();
    this.http.get<{ id: number; name: string }[]>(`${environment.apiBaseUrl}/places`)
      .subscribe(data => this.places = data);
  }

  loadPosts(): void {
    this.postService.getAllPosts().subscribe({
      next: (data) => { this.posts = data.map(p => this.mapToVM(p)); },
      error: (err) => console.error('Błąd pobierania postów:', err)
    });
  }

  private mapToVM(p: PostResponse): PostVM {
    const color = AVATAR_COLORS[p.authorId % AVATAR_COLORS.length];
    const initials = (p.authorFirstName?.[0] ?? '') + (p.authorLastName?.[0] ?? '');
    return {
      id: p.id,
      authorName: `${p.authorFirstName} ${p.authorLastName}`,
      initials,
      avatarBg: color.bg,
      avatarColor: color.fg,
      timeAgo: this.formatTime(p.createdAt),
      placeName: p.placeName,
      placeId: p.placeId,
      content: p.content,
      // imageUrl: p.imageUrl, // ← odkomentuj gdy chmura gotowa
      likes: p.likesCount,
      userLiked: p.likedByMe,
      comments: p.commentsCount,
      newComment: '',
      showComments: false,
      commentsList: [],
      isMyPost: p.isMyPost,
      showMenu: false
    };
  }

  toggleLike(post: PostVM): void {
    if (post.userLiked) {
      post.userLiked = false; post.likes--;
      this.postService.unlikePost(post.id).subscribe({
        error: () => { post.userLiked = true; post.likes++; }
      });
    } else {
      post.userLiked = true; post.likes++;
      this.postService.likePost(post.id).subscribe({
        error: () => { post.userLiked = false; post.likes--; }
      });
    }
  }

  toggleComments(post: PostVM): void {
    post.showComments = !post.showComments;
    if (post.showComments && post.commentsList.length === 0) {
      this.postService.getComments(post.id).subscribe({
        next: (data) => { post.commentsList = data; },
        error: (err) => console.error(err)
      });
    }
  }

  postComment(post: PostVM): void {
    if (!post.newComment.trim()) return;
    this.postService.addComment(post.id, post.newComment).subscribe({
      next: (c) => {
        post.commentsList.push(c);
        post.comments++;
        post.newComment = '';
      },
      error: (err) => console.error(err)
    });
  }

  deletePost(post: PostVM): void {
    this.postService.deletePost(post.id).subscribe({
      next: () => { this.posts = this.posts.filter(p => p.id !== post.id); },
      error: (err) => console.error('Błąd usuwania posta:', err)
    });
  }

  deleteComment(post: PostVM, comment: PostCommentResponse): void {
    this.postService.deleteComment(post.id, comment.id).subscribe({
      next: () => {
        post.commentsList = post.commentsList.filter(c => c.id !== comment.id);
        post.comments--;
      },
      error: (err) => console.error('Błąd usuwania komentarza:', err)
    });
  }

  submitPost(): void {
    if (!this.newPostContent.trim() || !this.selectedPlaceId || this.submitting) return;
    this.submitting = true;
    this.postService.createPost(this.selectedPlaceId, this.newPostContent).subscribe({
      next: (p) => { this.posts.unshift(this.mapToVM(p)); this.closeModal(); this.submitting = false; },
      error: () => { this.submitting = false; }
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.newPostContent = '';
    this.selectedPlaceId = null;
    // this.selectedImagePreview = ''; // ← odkomentuj gdy chmura gotowa
  }

  // onImageSelected(event: Event): void { // ← odkomentuj gdy chmura gotowa
  //   const file = (event.target as HTMLInputElement).files?.[0];
  //   if (!file) return;
  //   const reader = new FileReader();
  //   reader.onload = () => { this.selectedImagePreview = reader.result as string; };
  //   reader.readAsDataURL(file);
  // }

  formatTime(dateStr: string): string {
    const diff = Date.now() - new Date(dateStr).getTime();
    const min = Math.floor(diff / 60000);
    if (min < 1) return 'przed chwilą';
    if (min < 60) return `${min} min temu`;
    const h = Math.floor(min / 60);
    if (h < 24) return `${h} godz. temu`;
    return `${Math.floor(h / 24)} dni temu`;
  }

  trackById(_: number, post: PostVM): number { return post.id; }
}
