import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PostResponse {
  id: number;
  content: string;
  // imageUrl?: string; // ← odkomentuj gdy chmura gotowa
  createdAt: string;
  placeId: number;
  placeName: string;
  authorId: number;
  authorFirstName: string;
  authorLastName: string;
  likesCount: number;
  likedByMe: boolean;
  commentsCount: number;
  isMyPost: boolean;
}

export interface PostCommentResponse {
  id: number;
  content: string;
  createdAt: string;
  authorId: number;
  authorFirstName: string;
  authorLastName: string;
  isMyComment: boolean;
  showMenu?: boolean;
}

@Injectable({ providedIn: 'root' })
export class PostService {
  private base = `${environment.apiBaseUrl}/posts`;

  constructor(private http: HttpClient) {}

  getAllPosts(): Observable<PostResponse[]> {
    return this.http.get<PostResponse[]>(this.base);
  }

  createPost(idPlace: number, content: string): Observable<PostResponse> {
    return this.http.post<PostResponse>(this.base, { idPlace, content });
  }

  likePost(postId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/${postId}/like`, {});
  }

  unlikePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${postId}/like`);
  }

  getComments(postId: number): Observable<PostCommentResponse[]> {
    return this.http.get<PostCommentResponse[]>(`${this.base}/${postId}/comments`);
  }

  addComment(postId: number, content: string): Observable<PostCommentResponse> {
    return this.http.post<PostCommentResponse>(`${this.base}/${postId}/comments`, { content });
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${postId}`);
  }

  deleteComment(postId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${postId}/comments/${commentId}`);
  }
}
