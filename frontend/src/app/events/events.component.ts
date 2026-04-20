import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EventService, EventResponse, PlaceOption, CommentResponse } from '../core/event.service';

export interface EventVM {
  id: number;
  nameOfEvent: string;
  dateOfEvent: string;
  comment?: string;
  placeName: string;
  placeId: number;
  organizerName: string;
  participantCount: number;
  joinedByMe: boolean;
  likesCount: number;
  likedByMe: boolean;
  interestedCount: number;
  interestedByMe: boolean;
  commentsCount: number;
  showComments: boolean;
  comments: CommentResponse[];
  newComment: string;
  organizedByMe: boolean;
  showMenu: boolean;
}

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  events: EventVM[] = [];
  places: PlaceOption[] = [];
  showModal = false;
  modalName = '';
  modalDate = '';
  modalComment = '';
  modalPlaceId: number | null = null;
  submitting = false;

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.loadEvents();
    this.loadPlaces();
  }

  loadEvents(): void {
    this.eventService.getAllEvents().subscribe({
      next: (data) => { this.events = data.map(e => this.mapToVM(e)); },
      error: (err) => console.error('Błąd:', err)
    });
  }

  loadPlaces(): void {
    this.eventService.getPlaces().subscribe({
      next: (data) => { this.places = data; },
      error: (err) => console.error('Błąd pobierania miejsc:', err)
    });
  }

  private mapToVM(e: EventResponse): EventVM {
    return {
      id: e.id,
      nameOfEvent: e.nameOfEvent,
      dateOfEvent: e.dateOfEvent,
      comment: e.comment,
      placeName: e.placeName,
      placeId: e.placeId,
      organizerName: (e.organizerFirstName ?? '') + ' ' + (e.organizerLastName ?? ''),
      participantCount: e.participantCount,
      joinedByMe: e.joinedByMe,
      likesCount: e.likesCount,
      likedByMe: e.likedByMe,
      interestedCount: e.interestedCount,
      interestedByMe: e.interestedByMe,
      commentsCount: e.commentsCount,
      showComments: false,
      comments: [],
      newComment: '',
      organizedByMe: e.organizedByMe,
      showMenu: false
    };
  }

  toggleJoin(event: EventVM): void {
    if (event.joinedByMe) {
      event.joinedByMe = false;
      event.participantCount--;
      this.eventService.leaveEvent(event.id).subscribe({
        error: () => { event.joinedByMe = true; event.participantCount++; }
      });
    } else {
      event.joinedByMe = true;
      event.participantCount++;
      this.eventService.joinEvent(event.id).subscribe({
        error: () => { event.joinedByMe = false; event.participantCount--; }
      });
    }
  }

  toggleLike(event: EventVM): void {
    if (event.likedByMe) {
      event.likedByMe = false;
      event.likesCount--;
      this.eventService.unlikeEvent(event.id).subscribe({
        error: () => { event.likedByMe = true; event.likesCount++; }
      });
    } else {
      event.likedByMe = true;
      event.likesCount++;
      this.eventService.likeEvent(event.id).subscribe({
        error: () => { event.likedByMe = false; event.likesCount--; }
      });
    }
  }

  toggleInterested(event: EventVM): void {
    if (event.interestedByMe) {
      event.interestedByMe = false;
      event.interestedCount--;
      this.eventService.unmarkInterested(event.id).subscribe({
        error: () => { event.interestedByMe = true; event.interestedCount++; }
      });
    } else {
      event.interestedByMe = true;
      event.interestedCount++;
      this.eventService.markInterested(event.id).subscribe({
        error: () => { event.interestedByMe = false; event.interestedCount--; }
      });
    }
  }

  toggleComments(event: EventVM): void {
    event.showComments = !event.showComments;
    if (event.showComments && event.comments.length === 0) {
      this.eventService.getComments(event.id).subscribe({
        next: (data) => { event.comments = data; },
        error: (err) => console.error('Błąd pobierania komentarzy:', err)
      });
    }
  }

  postComment(event: EventVM): void {
    if (!event.newComment.trim()) return;
    this.eventService.addComment(event.id, event.newComment).subscribe({
      next: (c) => {
        event.comments.push(c);
        event.commentsCount++;
        event.newComment = '';
      },
      error: (err) => console.error('Błąd dodawania komentarza:', err)
    });
  }

  submitEvent(): void {
    if (!this.modalName.trim() || !this.modalDate || !this.modalPlaceId || this.submitting) return;
    this.submitting = true;
    this.eventService.createEvent({
      nameOfEvent: this.modalName,
      idPlace: this.modalPlaceId,
      dateOfEvent: this.modalDate,
      comment: this.modalComment || undefined
    }).subscribe({
      next: (e) => { this.events.unshift(this.mapToVM(e)); this.closeModal(); this.submitting = false; },
      error: () => { this.submitting = false; }
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.modalName = '';
    this.modalDate = '';
    this.modalComment = '';
    this.modalPlaceId = null;
  }

  isToday(dateStr: string): boolean {
    return new Date(dateStr).toDateString() === new Date().toDateString();
  }

  isTomorrow(dateStr: string): boolean {
    const t = new Date(); t.setDate(t.getDate() + 1);
    return new Date(dateStr).toDateString() === t.toDateString();
  }

  deleteEvent(event: EventVM): void {
    this.eventService.deleteEvent(event.id).subscribe({
      next: () => { this.events = this.events.filter(e => e.id !== event.id); },
      error: (err) => console.error('Błąd usuwania:', err)
    });
  }

  deleteComment(event: EventVM, comment: CommentResponse): void {
    this.eventService.deleteComment(event.id, comment.id).subscribe({
      next: () => {
        event.comments = event.comments.filter(c => c.id !== comment.id);
        event.commentsCount--;
      },
      error: (err) => console.error('Błąd usuwania komentarza:', err)
    });
  }

  formatDay(dateStr: string): string { return new Date(dateStr).getDate().toString(); }
  formatMonth(dateStr: string): string { return new Date(dateStr).toLocaleDateString('pl-PL', { month: 'short' }); }
  formatHour(dateStr: string): string { return new Date(dateStr).toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' }); }
  formatFullDate(dateStr: string): string { return new Date(dateStr).toLocaleDateString('pl-PL', { day: 'numeric', month: 'long', year: 'numeric' }); }
  trackById(_: number, event: EventVM): number { return event.id; }
}
