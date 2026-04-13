import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
// import { EventService } from '../core/event.service'; // ← odkomentuj gdy API gotowe

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
  imageUrl?: string;
  likes: number;
  comments: number;
  userLiked: boolean;
  interestedCount: number;
  interestedByMe: boolean;
  newComment: string;
}

// ─── MOCK DATA ── usuń ten blok gdy API gotowe ───────────────────────────────
const today = new Date();
const tomorrow = new Date(today); tomorrow.setDate(today.getDate() + 1);
const nextWeek = new Date(today); nextWeek.setDate(today.getDate() + 5);

const MOCK_EVENTS: EventVM[] = [
  {
    id: 1,
    nameOfEvent: 'Wieczór gier planszowych',
    dateOfEvent: new Date(new Date(today).setHours(18, 30)).toISOString(),
    comment: 'Przynosimy swoje ulubione gry, zaczynamy od Catan.',
    placeName: 'Kawiarnia Zielona',
    placeId: 5,
    organizerName: 'Kasia N.',
    participantCount: 6,
    joinedByMe: false,
    likes: 0, comments: 0, userLiked: false,
    interestedCount: 14, interestedByMe: false, newComment: ''
  },
  {
    id: 2,
    nameOfEvent: 'Poranny jogging po kampusie',
    dateOfEvent: new Date(new Date(tomorrow).setHours(7, 0)).toISOString(),
    comment: 'Spotykamy się przy fontannie. Dystans ok. 5 km.',
    placeName: 'Fontanna SGGW',
    placeId: 3,
    organizerName: 'Piotr W.',
    participantCount: 12,
    joinedByMe: false,
    likes: 10, comments: 0, userLiked: false,
    interestedCount: 15, interestedByMe: false, newComment: ''
  },
  {
    id: 3,
    nameOfEvent: 'Wspólna nauka przed egzaminem',
    dateOfEvent: new Date(new Date(nextWeek).setHours(14, 0)).toISOString(),
    comment: 'Matematyka i statystyka. Weź notatki!',
    placeName: 'Biblioteka Główna',
    placeId: 1,
    organizerName: 'Ty',
    participantCount: 4,
    joinedByMe: false,
    likes: 8, comments: 2, userLiked: false,
    interestedCount: 24, interestedByMe: false, newComment: ''
  }
];
// ────────────────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  events: EventVM[] = [];
  showModal = false;
  modalName = '';
  modalDate = '';
  modalComment = '';
  submitting = false;
  modalImageUrl = '';
  selectedImagePreview = '';

  // constructor(private eventService: EventService) {} // ← odkomentuj gdy API gotowe
  constructor() {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    // ── MOCK ── usuń i zamień gdy API gotowe ─────────────────────────────────
    this.events = MOCK_EVENTS.map(e => ({ ...e }));

    // ── API ── odkomentuj gdy API gotowe ─────────────────────────────────────
    // this.eventService.getAllEvents().subscribe(data => {
    //   this.events = data.map(e => ({ ...e }));
    // });
  }

  toggleJoin(event: EventVM): void {
    event.joinedByMe = !event.joinedByMe;
    event.participantCount += event.joinedByMe ? 1 : -1;
    // ── API ── odkomentuj gdy API gotowe ─────────────────────────────────────
    // if (event.joinedByMe) this.eventService.joinEvent(event.id).subscribe();
    // else this.eventService.leaveEvent(event.id).subscribe();
  }

  submitEvent(): void {
    if (!this.modalName.trim() || !this.modalDate || this.submitting) return;
    this.submitting = true;

    // ── MOCK ── usuń gdy API gotowe ──────────────────────────────────────────
    const newEvent: EventVM = {
      id: Date.now(),
      nameOfEvent: this.modalName,
      dateOfEvent: this.modalDate,
      comment: this.modalComment || undefined,
      placeName: 'Nieznane miejsce',
      placeId: 0,
      organizerName: 'Ty',
      participantCount: 1,
      joinedByMe: false,
      imageUrl: this.modalImageUrl || undefined,
      likes: 0, comments: 0, userLiked: false,
      interestedCount: 0, interestedByMe: false, newComment: ''
    };
    this.events.unshift(newEvent);
    this.closeModal();
    this.submitting = false;

    // ── API ── odkomentuj gdy API gotowe ─────────────────────────────────────
    // this.eventService.createEvent({
    //   nameOfEvent: this.modalName,
    //   idPlace: 1,
    //   dateOfEvent: this.modalDate,
    //   comment: this.modalComment || undefined
    // }).subscribe({
    //   next: (e) => { this.events.unshift(e); this.closeModal(); this.submitting = false; },
    //   error: () => { this.submitting = false; }
    // });
  }

  closeModal(): void {
    this.showModal = false;
    this.modalName = '';
    this.modalDate = '';
    this.modalComment = '';
    this.modalImageUrl = '';
    this.selectedImagePreview = '';
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

  trackById(_: number, event: EventVM): number {
    return event.id;
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
  onImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      this.selectedImagePreview = reader.result as string;
      this.modalImageUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }
  formatFullDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('pl-PL', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }
}
