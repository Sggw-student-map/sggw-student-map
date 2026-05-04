import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import * as L from 'leaflet';
import { PlacePin, PlaceService, CreatePlaceRequest, NavigationResponse, PlaceSortOption } from '../core/place.service';
import { EventService, EventResponse } from '../core/event.service';
import { UserStateService } from '../core/user-state.service';
import { Subject, of, debounceTime, distinctUntilChanged, switchMap, forkJoin, catchError } from 'rxjs';
import { NavbarComponent } from '../shared/navbar/navbar.component';

interface SortOptionConfig {
  id: PlaceSortOption;
  label: string;
  description: string;
  dotClass: string;
}

const HIGHLIGHT_RATING_THRESHOLD = 4.5;
// Wydarzenie liczy się jako "aktualne", jeśli jego data jest nie wcześniej niż 24h temu
const EVENT_PAST_TOLERANCE_MS = 24 * 60 * 60 * 1000;

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NavbarComponent],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements OnInit, AfterViewInit, OnDestroy {
  private map!: L.Map;
  private markerLayer = L.layerGroup();
  private popupOpenHandler!: (e: L.PopupEvent) => void;
  private markerMap: { [id: number]: L.Marker } = {};
  private searchSubject = new Subject<string>();

  searchQuery = '';
  searchResults: PlacePin[] = [];
  suggestedPlaces: PlacePin[] = [];
  searchFocused = false;
  searchLoading = false;
  private blurTimeoutId: number | undefined;

  addPlaceMode = false;
  showPlaceForm = false;
  newPlace: CreatePlaceRequest = { name: '', latitude: 0, longitude: 0, description: '' };
  private tempMarker?: L.Marker;
  formError = '';

  deletePlaceMode = false;
  placeToDelete: PlacePin | null = null;
  deleteError = '';
  deleting = false;

  locating = false;
  locateError = '';
  private userLocationMarker?: L.Marker;
  private userLocationAccuracyCircle?: L.Circle;

  readonly sortOptions: SortOptionConfig[] = [
    { id: 'RECENT', label: 'Ostatnie', description: 'Najnowsze pinezki', dotClass: 'bg-cyan-200' },
    { id: 'HIGHEST_RATED', label: 'Najwyżej oceniane', description: 'Posortuj wg średniej oceny', dotClass: 'bg-pink-200' },
    { id: 'LOWEST_RATED', label: 'Najniżej oceniane', description: 'Pokaż najgorzej oceniane', dotClass: 'bg-orange-200' },
  ];

  selectedSort: PlaceSortOption = 'RECENT';
  minRating = 0;
  onlyRated = false;
  limit: number | null = null;
  readonly limitOptions: { value: number | null; label: string }[] = [
    { value: null, label: 'Wszystkie' },
    { value: 5, label: '5' },
    { value: 10, label: '10' },
    { value: 20, label: '20' },
    { value: 50, label: '50' },
  ];

  visibleCount = 0;
  totalCount = 0;
  isRatingFilterActive = false;

  private blueIcon!: L.Icon;
  private highlightIcon!: L.Icon;
  private reloadSubject = new Subject<void>();

  constructor(
    private readonly placeService: PlaceService,
    private readonly userState: UserStateService,
    private readonly router: Router,
    private readonly eventService: EventService
  ) {}

  private goToFeedAndCreatePost(placeId: number): void {
  this.router.navigate(['/feed'], { 
    queryParams: { openNewPost: 'true', placeId: placeId } 
  });
}


private deletePin(placeId: number): void {
  // Zakładając, że masz taką metodę w PlaceService. 
  // Jeśli nie, musisz ją dodać w core/place.service.ts
  this.placeService.delete(placeId).subscribe({
    next: () => {
      // Zamyka popup i przeładowuje pinezki
      this.map.closePopup();
      this.scheduleReload();
    },
    error: (err) => console.error('Błąd usuwania pinezki', err)
  });
}

  ngOnInit(): void {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (!query.trim()) {
          this.searchLoading = false;
          return of([] as PlacePin[]);
        }
        return this.placeService.search(query);
      })
    ).subscribe(results => {
      this.searchResults = results;
      this.searchLoading = false;
    });

    this.reloadSubject.pipe(debounceTime(150)).subscribe(() => this.loadPins());
  }

  ngAfterViewInit(): void {
    this.blueIcon = L.icon({
      iconUrl: '/blue-mark.svg',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
    });
    this.highlightIcon = L.icon({
      iconUrl: '/purple-mark.svg',
      iconSize: [30, 50],
      iconAnchor: [15, 50],
      popupAnchor: [1, -42],
    });
    this.initMap();
    this.loadPins();
    this.loadSuggestedPlaces();
  }

  ngOnDestroy(): void {
    if (this.blurTimeoutId !== undefined) {
      window.clearTimeout(this.blurTimeoutId);
    }
    if (this.map) {
      this.map.off('popupopen', this.popupOpenHandler);
      if (this.userLocationMarker) {
        this.map.removeLayer(this.userLocationMarker);
      }
      if (this.userLocationAccuracyCircle) {
        this.map.removeLayer(this.userLocationAccuracyCircle);
      }
      this.map.remove();
    }
  }

  zoomIn(): void {
    this.map.zoomIn(1);
  }

  zoomOut(): void {
    this.map.zoomOut(1);
  }

  resetView(): void {
    this.map.setView([52.161, 21.047], 16, { animate: true });
  }

  toggleAddPlaceMode(): void {
    this.addPlaceMode = !this.addPlaceMode;
    if (this.addPlaceMode) {
      this.exitDeleteMode();
    } else {
      this.cancelPlaceForm();
    }
  }

  toggleDeleteMode(): void {
    this.deletePlaceMode = !this.deletePlaceMode;
    if (this.deletePlaceMode) {
      this.addPlaceMode = false;
      this.cancelPlaceForm();
      this.deleteError = '';
    } else {
      this.placeToDelete = null;
      this.deleteError = '';
    }
  }

  private exitDeleteMode(): void {
    this.deletePlaceMode = false;
    this.placeToDelete = null;
    this.deleteError = '';
  }

  cancelDelete(): void {
    this.placeToDelete = null;
    this.deleteError = '';
  }

  confirmDelete(): void {
    if (!this.placeToDelete || this.deleting) {
      return;
    }
    const id = this.placeToDelete.id;
    this.deleting = true;
    this.deleteError = '';
    this.placeService.delete(id).subscribe({
      next: () => {
        this.deleting = false;
        this.placeToDelete = null;
        this.deletePlaceMode = false;
        this.loadPins();
      },
      error: () => {
        this.deleting = false;
        this.deleteError = 'Nie udało się usunąć miejsca. Zaloguj się i spróbuj ponownie.';
      },
    });
  }

  locateMe(): void {
    if (this.locating) {
      return;
    }
    if (!('geolocation' in navigator)) {
      this.locateError = 'Twoja przeglądarka nie obsługuje geolokalizacji.';
      window.setTimeout(() => (this.locateError = ''), 3500);
      return;
    }
    this.locating = true;
    this.locateError = '';
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        this.locating = false;
        const { latitude, longitude, accuracy } = pos.coords;
        this.placeUserLocationMarker(latitude, longitude, accuracy);
        this.map.flyTo([latitude, longitude], Math.max(this.map.getZoom(), 17), {
          duration: 0.7,
        });
      },
      (err) => {
        this.locating = false;
        this.locateError = err.code === err.PERMISSION_DENIED
          ? 'Brak zgody na dostęp do lokalizacji.'
          : 'Nie udało się ustalić lokalizacji.';
        window.setTimeout(() => (this.locateError = ''), 3500);
      },
      { enableHighAccuracy: true, timeout: 8000, maximumAge: 30_000 }
    );
  }

  private placeUserLocationMarker(lat: number, lng: number, accuracy: number): void {
    if (this.userLocationMarker) {
      this.map.removeLayer(this.userLocationMarker);
    }
    if (this.userLocationAccuracyCircle) {
      this.map.removeLayer(this.userLocationAccuracyCircle);
    }

    const userIcon = L.divIcon({
      className: 'user-location-icon',
      html: '<span class="user-location-pulse"></span><span class="user-location-dot"></span>',
      iconSize: [22, 22],
      iconAnchor: [11, 11],
    });

    this.userLocationAccuracyCircle = L.circle([lat, lng], {
      radius: Math.min(accuracy, 200),
      color: '#a855f7',
      weight: 1,
      fillColor: '#a855f7',
      fillOpacity: 0.12,
      interactive: false,
    }).addTo(this.map);

    this.userLocationMarker = L.marker([lat, lng], {
      icon: userIcon,
      interactive: false,
      keyboard: false,
    }).addTo(this.map);
  }

  onMapClick(e: L.LeafletMouseEvent): void {
    if (this.deletePlaceMode) {
      this.placeToDelete = null;
      this.deleteError = '';
      return;
    }
    if (!this.addPlaceMode) return;

    this.removeTempMarker();

    this.newPlace = {
      name: '',
      latitude: e.latlng.lat,
      longitude: e.latlng.lng,
      description: '',
    };

    const redIcon = L.icon({
      iconUrl: '/red-mark.svg',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
    });

    this.tempMarker = L.marker([e.latlng.lat, e.latlng.lng], { icon: redIcon }).addTo(this.map);
    this.showPlaceForm = true;
    this.formError = '';
  }

  submitPlace(): void {
    if (!this.newPlace.name.trim()) {
      this.formError = 'Nazwa jest wymagana';
      return;
    }

    this.placeService.create(this.newPlace).subscribe({
      next: () => {
        this.cancelPlaceForm();
        this.addPlaceMode = false;
        this.loadPins();
      },
      error: () => {
        this.formError = 'Nie udało się dodać miejsca. Zaloguj się i spróbuj ponownie.';
      },
    });
  }

  cancelPlaceForm(): void {
    this.showPlaceForm = false;
    this.newPlace = { name: '', latitude: 0, longitude: 0, description: '' };
    this.formError = '';
    this.removeTempMarker();
  }

  onSearchInput(): void {
    if (this.searchQuery.trim()) {
      this.searchLoading = true;
    } else {
      this.searchResults = [];
      this.searchLoading = false;
    }
    this.searchSubject.next(this.searchQuery);
  }

  onSearchFocus(): void {
    if (this.blurTimeoutId !== undefined) {
      window.clearTimeout(this.blurTimeoutId);
      this.blurTimeoutId = undefined;
    }
    this.searchFocused = true;
    if (!this.searchQuery.trim() && this.suggestedPlaces.length === 0) {
      this.loadSuggestedPlaces();
    }
  }

  onSearchBlur(): void {
    if (this.blurTimeoutId !== undefined) {
      window.clearTimeout(this.blurTimeoutId);
    }
    this.blurTimeoutId = window.setTimeout(() => {
      this.searchFocused = false;
      this.blurTimeoutId = undefined;
    }, 180);
  }

  selectSearchResult(place: PlacePin): void {
    this.searchQuery = place.name;
    this.searchResults = [];
    this.searchFocused = false;

    const marker = this.markerMap[place.id];
    if (marker) {
      this.map.flyTo([place.latitude, place.longitude], Math.max(this.map.getZoom(), 17), {
        duration: 0.6,
      });
      marker.openPopup();
    }
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searchResults = [];
    this.searchLoading = false;
    this.searchSubject.next('');
  }

  get hasSearchQuery(): boolean {
    return this.searchQuery.trim().length > 0;
  }

  get searchDropdownItems(): PlacePin[] {
    return this.hasSearchQuery ? this.searchResults : this.suggestedPlaces;
  }

  get isSearchDropdownOpen(): boolean {
    if (!this.searchFocused) {
      return false;
    }
    if (this.hasSearchQuery) {
      return true;
    }
    return this.suggestedPlaces.length > 0;
  }

  private loadSuggestedPlaces(): void {
    this.placeService.getAll({ sort: 'HIGHEST_RATED', limit: 6 }).subscribe({
      next: (places: PlacePin[]) => {
        this.suggestedPlaces = places;
      },
      error: () => {
        this.suggestedPlaces = [];
      },
    });
  }

  selectSort(option: PlaceSortOption): void {
    if (this.selectedSort === option) {
      return;
    }
    this.selectedSort = option;
    this.scheduleReload();
  }

  onMinRatingChange(value: number | string): void {
    const parsed = typeof value === 'string' ? parseFloat(value) : value;
    this.minRating = Number.isFinite(parsed) ? Math.max(0, Math.min(5, parsed)) : 0;
    this.scheduleReload();
  }

  toggleOnlyRated(): void {
    this.onlyRated = !this.onlyRated;
    this.scheduleReload();
  }

  selectLimit(value: number | null): void {
    if (this.limit === value) {
      return;
    }
    this.limit = value;
    this.scheduleReload();
  }

  resetFilters(): void {
    this.minRating = 0;
    this.onlyRated = false;
    this.selectedSort = 'RECENT';
    this.limit = null;
    this.scheduleReload();
  }

  private scheduleReload(): void {
    this.isRatingFilterActive =
      this.selectedSort !== 'RECENT' ||
      this.minRating > 0 ||
      this.onlyRated ||
      this.limit !== null;
    this.reloadSubject.next();
  }

  private removeTempMarker(): void {
    if (this.tempMarker) {
      this.map.removeLayer(this.tempMarker);
      this.tempMarker = undefined;
    }
  }

  private buildStarsHtml(rating: number): string {
    const full = Math.floor(rating);
    const half = rating - full >= 0.25 && rating - full < 0.75;
    const empty = 5 - full - (half ? 1 : 0);
    const star = (type: 'full' | 'half' | 'empty'): string => {
      if (type === 'full')  return `<span style="color:#f59e0b;font-size:14px">★</span>`;
      if (type === 'half')  return `<span style="color:#f59e0b;font-size:14px;opacity:.55">★</span>`;
      return `<span style="color:#d1d5db;font-size:14px">★</span>`;
    };
    return Array(full).fill(star('full')).join('') +
           (half ? star('half') : '') +
           Array(empty).fill(star('empty')).join('');
  }

  private buildPopupHtml(pin: PlacePin, events: EventResponse[] = []): string {
    const name = this.escapeHtml(pin.name);
    const desc = pin.description ? this.escapeHtml(pin.description) : '';
    const hasRating = pin.averageRating != null;
    const ratingHtml = hasRating
      ? `<div style="display:flex;align-items:center;gap:5px;margin-bottom:8px">
           ${this.buildStarsHtml(pin.averageRating!)}
           <span style="font-size:11px;font-weight:700;color:#92400e;background:#fef3c7;padding:1px 6px;border-radius:99px">${pin.averageRating!.toFixed(1)}</span>
         </div>`
      : `<div style="font-size:11px;color:#9ca3af;margin-bottom:8px">Brak ocen</div>`;

    const eventsHtml = events.length > 0 ? this.buildEventsBlockHtml(events) : '';

    return `
      <div style="min-width:190px;font-family:system-ui,sans-serif">
        <div style="font-weight:700;font-size:14px;margin-bottom:5px">${name}</div>
        ${ratingHtml}
        ${desc ? `<div style="color:#555;font-size:12px;margin-bottom:8px">${desc}</div>` : ''}
        ${eventsHtml}
        <button
          class="nav-to-gmaps-btn"
          data-place-id="${pin.id}"
          style="
            display:flex;align-items:center;gap:6px;
            width:100%;padding:7px 12px;
            color:#fff;
            border:none;border-radius:8px;
            font-size:12px;font-weight:600;
            cursor:pointer;
          "
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
            <circle cx="12" cy="9" r="2.5"/>
          </svg>
          Pokaż trasę w Google Maps
        </button>
        <button class="add-post-btn" data-place-id="${pin.id}" style="margin-top:5px; width:100%; padding:7px; background:#e0f2fe; color:#0369a1; border:none; border-radius:8px; cursor:pointer;">
        Dodaj post z tego miejsca
      </button>
      <button class="delete-pin-btn" data-place-id="${pin.id}" style="margin-top:5px; width:100%; padding:7px; background:#fee2e2; color:#b91c1c; border:none; border-radius:8px; cursor:pointer;">
        Usuń pinezkę
      </button>
      </div>`;
  }

  private handleNavigationClick(placeId: number): void {
    this.placeService.getNavigation(placeId).subscribe({
      next: (nav: NavigationResponse) => {
        window.open(nav.googleMapsUrl, '_blank', 'noopener,noreferrer');
      },
      error: () => {
        console.error('Failed to get navigation for place', placeId);
      },
    });
  }

  /**
   * Grupuje "aktualne" wydarzenia po miejscu. Wydarzenie liczy się jako aktualne,
   * jeśli jego `dateOfEvent` jest nie wcześniej niż 24h temu (czyli również takie,
   * które właśnie trwa lub odbywa się w przyszłości). Wewnątrz każdej grupy
   * wydarzenia są posortowane chronologicznie.
   *
   * Używamy `Record` zamiast `Map<>`, ponieważ nazwa tej klasy (`Map`) przesłania
   * globalny typ `Map` z ES2015.
   */
  private groupCurrentEventsByPlace(events: EventResponse[]): Record<number, EventResponse[]> {
    const cutoff = Date.now() - EVENT_PAST_TOLERANCE_MS;
    const grouped: Record<number, EventResponse[]> = {};

    for (const ev of events) {
      const eventTime = new Date(ev.dateOfEvent).getTime();
      if (Number.isNaN(eventTime) || eventTime < cutoff) {
        continue;
      }
      const list = grouped[ev.placeId] ?? [];
      list.push(ev);
      grouped[ev.placeId] = list;
    }

    for (const key of Object.keys(grouped)) {
      grouped[Number(key)].sort(
        (a, b) => new Date(a.dateOfEvent).getTime() - new Date(b.dateOfEvent).getTime()
      );
    }
    return grouped;
  }

  /**
   * Tworzy `L.divIcon` zawierający bazową grafikę pinezki oraz badge informujący,
   * że w danym miejscu odbywa się wydarzenie. Jeśli jest więcej niż jedno wydarzenie,
   * w badge'u wyświetlana jest liczba zamiast gwiazdki.
   */
  private buildEventBadgeIcon(baseIcon: L.Icon, eventCount: number): L.DivIcon {
    const opts = baseIcon.options;
    const iconUrl = opts.iconUrl as string;
    const [w, h] = (opts.iconSize as L.PointTuple) ?? [25, 41];
    const badgeContent = eventCount > 1
      ? `<span class="pin-event-badge-count${eventCount > 9 ? ' pin-event-badge-count-multi' : ''}">${eventCount > 99 ? '99+' : eventCount}</span>`
      : '';

    return L.divIcon({
      className: 'pin-with-event',
      html: `
        <img src="${iconUrl}" width="${w}" height="${h}" alt="" draggable="false" />
        <span class="pin-event-badge" title="W tym miejscu odbywa się wydarzenie">
          ${badgeContent}
        </span>
      `,
      iconSize: opts.iconSize as L.PointTuple,
      iconAnchor: opts.iconAnchor as L.PointTuple,
      popupAnchor: opts.popupAnchor as L.PointTuple,
    });
  }

  /**
   * Sekcja popupu z najbliższymi wydarzeniami w danym miejscu (max 3).
   */
  private buildEventsBlockHtml(events: EventResponse[]): string {
    const visible = events.slice(0, 3);
    const rows = visible.map((ev) => {
      const name = this.escapeHtml(ev.nameOfEvent ?? 'Wydarzenie');
      const when = this.formatEventDate(ev.dateOfEvent);
      return `
        <div style="display:flex;gap:6px;align-items:flex-start;line-height:1.25">
          <span style="color:#dc2626;font-size:11px;line-height:1.4">●</span>
          <div style="min-width:0;flex:1">
            <div style="font-size:12px;font-weight:600;color:#111827;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">${name}</div>
            <div style="font-size:11px;color:#6b7280">${when}</div>
          </div>
        </div>`;
    }).join('');

    const moreLabel = events.length > visible.length
      ? `<div style="font-size:11px;color:#9ca3af;margin-top:4px">+ ${events.length - visible.length} więcej</div>`
      : '';

    return `
      <div style="background:#fef2f2;border:1px solid #fecaca;border-radius:10px;padding:8px 10px;margin-bottom:10px;display:flex;flex-direction:column;gap:6px">
        <div style="display:flex;align-items:center;gap:6px;font-size:11px;font-weight:700;color:#b91c1c;text-transform:uppercase;letter-spacing:0.04em">
          <span>★</span>
          <span>Nadchodzące wydarzenia</span>
        </div>
        ${rows}
        ${moreLabel}
      </div>`;
  }

  private formatEventDate(dateStr: string): string {
    const date = new Date(dateStr);
    if (Number.isNaN(date.getTime())) return '';
    const now = new Date();
    const isSameDay = date.toDateString() === now.toDateString();
    const tomorrow = new Date(now);
    tomorrow.setDate(now.getDate() + 1);
    const isTomorrow = date.toDateString() === tomorrow.toDateString();

    const time = date.toLocaleTimeString('pl-PL', { hour: '2-digit', minute: '2-digit' });
    if (isSameDay) return `Dziś, ${time}`;
    if (isTomorrow) return `Jutro, ${time}`;
    const day = date.toLocaleDateString('pl-PL', { day: 'numeric', month: 'short' });
    return `${day}, ${time}`;
  }

  private loadPins(): void {
    const pins$ = this.placeService.getAll({
      sort: this.selectedSort,
      minRating: this.minRating,
      onlyRated: this.onlyRated,
      limit: this.limit,
    });

    // Eventy wymagają zalogowanego użytkownika; w razie błędu (brak auth, sieć) traktujemy jak pustą listę,
    // żeby pinezki i tak się załadowały.
    const events$ = this.eventService.getAllEvents().pipe(
      catchError(() => of([] as EventResponse[]))
    );

    forkJoin({ pins: pins$, events: events$ }).subscribe({
      next: ({ pins, events }) => {
        const eventsByPlace = this.groupCurrentEventsByPlace(events);

        this.markerLayer.clearLayers();
        this.markerMap = {};
        this.visibleCount = pins.length;
        if (!this.isRatingFilterActive) {
          this.totalCount = pins.length;
        } else if (this.totalCount === 0) {
          this.totalCount = pins.length;
        }
        pins.forEach((pin) => {
          const placeEvents = eventsByPlace[pin.id] ?? [];
          const baseIcon = pin.averageRating != null && pin.averageRating >= HIGHLIGHT_RATING_THRESHOLD
            ? this.highlightIcon
            : this.blueIcon;
          const icon = placeEvents.length > 0
            ? this.buildEventBadgeIcon(baseIcon, placeEvents.length)
            : baseIcon;
          let hoverCloseTimeout: number | undefined;
          let popupCloseAnimationTimeout: number | undefined;

          const clearCloseTimers = (): void => {
            if (hoverCloseTimeout) {
              window.clearTimeout(hoverCloseTimeout);
              hoverCloseTimeout = undefined;
            }
            if (popupCloseAnimationTimeout) {
              window.clearTimeout(popupCloseAnimationTimeout);
              popupCloseAnimationTimeout = undefined;
            }
          };

          const closePopupWithAnimation = (): void => {
            const popupElement = marker.getPopup()?.getElement();
            if (popupElement) {
              popupElement.classList.remove('is-visible');
              popupCloseAnimationTimeout = window.setTimeout(() => {
                marker.closePopup();
              }, 160);
              return;
            }
            marker.closePopup();
          };

          const scheduleClose = (): void => {
            clearCloseTimers();
            hoverCloseTimeout = window.setTimeout(() => {
              closePopupWithAnimation();
            }, 120);
          };

          const marker = L.marker([pin.latitude, pin.longitude], { icon })
            .bindPopup(this.buildPopupHtml(pin, placeEvents))
            .addTo(this.markerLayer);

          marker.on('mouseover', () => {
            if (this.deletePlaceMode) {
              return;
            }
            clearCloseTimers();
            marker.openPopup();
          });
          marker.on('mouseout', () => scheduleClose());

          marker.on('click', (event: L.LeafletMouseEvent) => {
            if (!this.deletePlaceMode) {
              return;
            }
            L.DomEvent.stopPropagation(event);
            clearCloseTimers();
            marker.closePopup();
            this.placeToDelete = pin;
            this.deleteError = '';
          });

          marker.on('popupopen', (e: L.PopupEvent) => {
            const popupElement = e.popup.getElement();
            if (!popupElement) return;

            popupElement.classList.add('map-hover-popup');
            requestAnimationFrame(() => popupElement.classList.add('is-visible'));
            popupElement.addEventListener('mouseenter', clearCloseTimers);
            popupElement.addEventListener('mouseleave', scheduleClose);
          });

          marker.on('popupclose', (e: L.PopupEvent) => {
            const popupElement = e.popup.getElement();
            if (!popupElement) return;
            popupElement.removeEventListener('mouseenter', clearCloseTimers);
            popupElement.removeEventListener('mouseleave', scheduleClose);
          });

          this.markerMap[pin.id] = marker;
        });
      },
      error: () => {
        this.markerLayer.clearLayers();
        this.markerMap = {};
        this.visibleCount = 0;
      },
    });
  }

  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  private initMap(): void {
    const sggwCoords: L.LatLngExpression = [52.161, 21.047];

    this.map = L.map('map', {
      center: sggwCoords,
      zoom: 16,
      zoomControl: false,
      dragging: true,
      keyboard: false,
      scrollWheelZoom: true,
      doubleClickZoom: true,
      boxZoom: false,
      touchZoom: true,
    });

    L.tileLayer(
      'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
      {
        attribution:
          'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-E nomenclature',
        maxZoom: 18,
      }
    ).addTo(this.map);

    this.markerLayer.addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => this.onMapClick(e));

    this.popupOpenHandler = (e: L.PopupEvent) => {
      const container = e.popup.getElement();
      if (!container) return;

      const navBtn = container.querySelector('.nav-to-gmaps-btn') as HTMLElement | null;
      if (navBtn) {
        const placeId = Number(navBtn.getAttribute('data-place-id'));
        if (placeId) {
          navBtn.addEventListener('click', (ev: Event) => {
            ev.stopPropagation();
            this.handleNavigationClick(placeId);
          });
        }
      }

      const addPostBtn = container.querySelector('.add-post-btn') as HTMLElement | null;
      if (addPostBtn) {
        const placeId = Number(addPostBtn.getAttribute('data-place-id'));
        if (placeId) {
          addPostBtn.addEventListener('click', (ev: Event) => {
            ev.stopPropagation();
            this.goToFeedAndCreatePost(placeId);
          });
        }
      }

      const deleteBtn = container.querySelector('.delete-pin-btn') as HTMLElement | null;
      if (deleteBtn) {
        const placeId = Number(deleteBtn.getAttribute('data-place-id'));
        if (placeId) {
          deleteBtn.addEventListener('click', (ev: Event) => {
            ev.stopPropagation();
            this.deletePin(placeId);
          });
        }
      }
    };
    this.map.on('popupopen', this.popupOpenHandler);
  }
}
