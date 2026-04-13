import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserSummary {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
}

export interface FriendshipResponse {
  user: UserSummary;
  status: string;
  direction: 'sent' | 'received' | 'accepted';
}

@Injectable({ providedIn: 'root' })
export class FriendService {
  private base = `${environment.apiBaseUrl}/friends`;

  constructor(private http: HttpClient) {}

  getFriends(): Observable<FriendshipResponse[]> {
    return this.http.get<FriendshipResponse[]>(this.base);
  }

  getPendingReceived(): Observable<FriendshipResponse[]> {
    return this.http.get<FriendshipResponse[]>(`${this.base}/pending/received`);
  }

  getPendingSent(): Observable<FriendshipResponse[]> {
    return this.http.get<FriendshipResponse[]>(`${this.base}/pending/sent`);
  }

  getInvitableUsers(): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.base}/invitable`);
  }

  sendInvite(targetUserId: number): Observable<FriendshipResponse> {
    return this.http.post<FriendshipResponse>(`${this.base}/invite`, { targetUserId });
  }

  acceptInvite(senderId: number): Observable<FriendshipResponse> {
    return this.http.put<FriendshipResponse>(`${this.base}/${senderId}/accept`, {});
  }

  deleteFriendship(otherUserId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${otherUserId}`);
  }
}
