import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FeedAuthor {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
}

export interface FeedPlace {
  id: number;
  name: string;
}

export interface FeedPost {
  id: number;
  author: FeedAuthor;
  place: FeedPlace | null;
  content: string;
  imageUrl: string | null;
  createdAt: string;
  likesCount: number;
  commentsCount: number;
  likedByMe: boolean;
  authoredByMe: boolean;
}

export interface FeedComment {
  id: number;
  postId: number;
  author: FeedAuthor;
  content: string;
  createdAt: string;
  authoredByMe: boolean;
}

export interface CreatePostRequest {
  placeId?: number | null;
  content: string;
  imageUrl?: string | null;
}

export interface CreateCommentRequest {
  content: string;
}

@Injectable({ providedIn: 'root' })
export class FeedService {
  private readonly base = `${environment.apiBaseUrl}/feed`;

  constructor(private readonly http: HttpClient) {}

  getFeed(page = 0, size = 50): Observable<FeedPost[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<FeedPost[]>(this.base, { params });
  }

  createPost(request: CreatePostRequest): Observable<FeedPost> {
    return this.http.post<FeedPost>(this.base, request);
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${postId}`);
  }

  like(postId: number): Observable<FeedPost> {
    return this.http.post<FeedPost>(`${this.base}/${postId}/like`, {});
  }

  unlike(postId: number): Observable<FeedPost> {
    return this.http.delete<FeedPost>(`${this.base}/${postId}/like`);
  }

  getComments(postId: number): Observable<FeedComment[]> {
    return this.http.get<FeedComment[]>(`${this.base}/${postId}/comments`);
  }

  addComment(postId: number, request: CreateCommentRequest): Observable<FeedComment> {
    return this.http.post<FeedComment>(`${this.base}/${postId}/comments`, request);
  }

  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/comments/${commentId}`);
  }
}
