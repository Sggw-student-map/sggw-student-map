import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EventService, EventResponse, PlaceOption } from '../core/event.service';

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
  newComment: string;
  likes: number;
  userLiked: boolean;
  comments: number;
  interestedCount: number;
  interestedByMe: boolean;
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
      newComment: '',
      likes: 0,
      userLiked: false,
      comments: 0,
      interestedCount: 0,
      interestedByMe: false
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
    event.userLiked = !event.userLiked;
    event.likes += event.userLiked ? 1 : -1;
  }

  toggleInterested(event: EventVM): void {
    event.interestedByMe = !event.interestedByMe;
    event.interestedCount += event.interestedByMe ? 1 : -1;
  }

  postComment(event: EventVM): void {
    if (!event.newComment.trim()) return;
    event.comments += 1;
    event.newComment = '';
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
      next: (e) => {
        this.events.unshift(this.mapToVM(e));
        this.closeModal();
        this.submitting = false;
      },
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

  formatDay(dateStr: string): string {
    return new Date(dateStr).getDate().toString();
  }

  formatMonth(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('pl-PL', { month: 'short' });
  }

  formatHour(dateStr: string): string {
    return new Date(dateStr).toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' });
  }

  formatFullDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('pl-PL', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }

  trackById(_: number, event: EventVM): number {
    return event.id;
  }
}
