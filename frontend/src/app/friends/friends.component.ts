import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FriendService, FriendshipResponse, UserSummary } from '../core/friend.service';

@Component({
  selector: 'app-friends',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.css'
})
export class FriendsComponent implements OnInit {

  activeTab: 'friends' | 'received' | 'sent' | 'invite' = 'friends';

  friends: FriendshipResponse[] = [];
  pendingReceived: FriendshipResponse[] = [];
  pendingSent: FriendshipResponse[] = [];
  invitableUsers: UserSummary[] = [];
  searchQuery = '';

  loading = false;
  message = '';

  get filteredInvitableUsers(): UserSummary[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.invitableUsers;
    return this.invitableUsers.filter(u =>
      u.username.toLowerCase().includes(q) ||
      u.firstName.toLowerCase().includes(q) ||
      u.lastName.toLowerCase().includes(q)
    );
  }

  constructor(private friendService: FriendService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.friendService.getFriends().subscribe(data => this.friends = data);
    this.friendService.getPendingReceived().subscribe(data => this.pendingReceived = data);
    this.friendService.getPendingSent().subscribe(data => this.pendingSent = data);
    this.friendService.getInvitableUsers().subscribe(data => {
      this.invitableUsers = data;
      this.loading = false;
    });
  }

  sendInvite(userId: number): void {
    this.friendService.sendInvite(userId).subscribe({
      next: () => {
        this.message = 'Invite sent!';
        this.loadAll();
      },
      error: () => this.message = 'Failed to send invite.'
    });
  }

  accept(senderId: number): void {
    this.friendService.acceptInvite(senderId).subscribe({
      next: () => {
        this.message = 'Friend accepted!';
        this.loadAll();
      },
      error: () => this.message = 'Failed to accept.'
    });
  }

  reject(userId: number): void {
    this.friendService.deleteFriendship(userId).subscribe({
      next: () => {
        this.message = 'Invite rejected.';
        this.loadAll();
      },
      error: () => this.message = 'Failed to reject.'
    });
  }

  removeFriend(userId: number): void {
    this.friendService.deleteFriendship(userId).subscribe({
      next: () => {
        this.message = 'Friend removed.';
        this.loadAll();
      },
      error: () => this.message = 'Failed to remove.'
    });
  }

  cancelInvite(userId: number): void {
    this.friendService.deleteFriendship(userId).subscribe({
      next: () => {
        this.message = 'Invite cancelled.';
        this.loadAll();
      },
      error: () => this.message = 'Failed to cancel.'
    });
  }

  setTab(tab: 'friends' | 'received' | 'sent' | 'invite'): void {
    this.activeTab = tab;
    this.message = '';
    this.searchQuery = '';
  }
}
