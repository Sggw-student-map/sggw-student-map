import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { PlacePin, PlaceService, CreatePlaceRequest, NavigationResponse, PlaceSortOption } from '../core/place.service';
import { UserStateService } from '../core/user-state.service';
import { CurrentUser } from '../core/auth.service';
import { RouterModule } from '@angular/router';
import { Subject, of, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

interface SortOptionConfig {
  id: PlaceSortOption;
  label: string;
  description: string;
  dotClass: string;
}

const HIGHLIGHT_RATING_THRESHOLD = 4.5;

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
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
  showSearchResults = false;

  addPlaceMode = false;
  showPlaceForm = false;
  newPlace: CreatePlaceRequest = { name: '', latitude: 0, longitude: 0, description: '' };
  private tempMarker?: L.Marker;
  formError = '';

  currentUser: CurrentUser | null = null;
  userInitials = '?';
  userDisplayName = 'Użytkownik';

  menuItems = [
    { name: 'Feed', color: 'bg-sky-200 text-sky-700',route: '/feed'  },
    { name: 'Wydarzenia', color: 'bg-green-200 text-green-700', route: '/events'  },
    { name: 'Opinie', color: 'bg-pink-200 text-pink-700', route: '/opinions' },
    { name: 'Znajomi', color: 'bg-orange-200 text-orange-700', route: '/friends' },
    { name: 'Powiadomienia', color: 'bg-yellow-200 text-yellow-700', route: '/notifications' }
  ];

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
    private readonly userState: UserStateService
  ) {}

  ngOnInit(): void {
    this.userState.loadUser().subscribe();
    this.userState.user$.subscribe((user: CurrentUser | null) => {
      this.currentUser = user;
      this.userInitials = this.userState.getInitials(user);
      this.userDisplayName = this.userState.getDisplayName(user);
    });

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (!query.trim()) {
          return of([]);
        }
        return this.placeService.search(query);
      })
    ).subscribe(results => {
      this.searchResults = results;
      this.showSearchResults = results.length > 0;
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
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.off('popupopen', this.popupOpenHandler);
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
    if (!this.addPlaceMode) {
      this.cancelPlaceForm();
    }
  }

  onMapClick(e: L.LeafletMouseEvent): void {
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
    this.searchSubject.next(this.searchQuery);
    if (!this.searchQuery.trim()) {
      this.searchResults = [];
      this.showSearchResults = false;
    }
  }

  selectSearchResult(place: PlacePin): void {
    this.searchQuery = place.name;
    this.searchResults = [];
    this.showSearchResults = false;

    const marker = this.markerMap[place.id];
    if (marker) {
      marker.openPopup();
    }
  }

  hideSearchResults(): void {
    setTimeout(() => {
      this.showSearchResults = false;
    }, 200);
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

  private buildPopupHtml(pin: PlacePin): string {
    const name = this.escapeHtml(pin.name);
    const desc = pin.description ? this.escapeHtml(pin.description) : '';
    const hasRating = pin.averageRating != null;
    const ratingHtml = hasRating
      ? `<div style="display:flex;align-items:center;gap:5px;margin-bottom:8px">
           ${this.buildStarsHtml(pin.averageRating!)}
           <span style="font-size:11px;font-weight:700;color:#92400e;background:#fef3c7;padding:1px 6px;border-radius:99px">${pin.averageRating!.toFixed(1)}</span>
         </div>`
      : `<div style="font-size:11px;color:#9ca3af;margin-bottom:8px">Brak ocen</div>`;

    return `
      <div style="min-width:190px;font-family:system-ui,sans-serif">
        <div style="font-weight:700;font-size:14px;margin-bottom:5px">${name}</div>
        ${ratingHtml}
        ${desc ? `<div style="color:#555;font-size:12px;margin-bottom:8px">${desc}</div>` : ''}
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

  private loadPins(): void {
    this.placeService.getAll({
      sort: this.selectedSort,
      minRating: this.minRating,
      onlyRated: this.onlyRated,
      limit: this.limit,
    }).subscribe({
      next: (pins: PlacePin[]) => {
        this.markerLayer.clearLayers();
        this.markerMap = {};
        this.visibleCount = pins.length;
        if (!this.isRatingFilterActive) {
          this.totalCount = pins.length;
        } else if (this.totalCount === 0) {
          this.totalCount = pins.length;
        }
        pins.forEach((pin) => {
          const icon = pin.averageRating != null && pin.averageRating >= HIGHLIGHT_RATING_THRESHOLD
            ? this.highlightIcon
            : this.blueIcon;
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
            .bindPopup(this.buildPopupHtml(pin))
            .addTo(this.markerLayer);

          marker.on('mouseover', () => {
            clearCloseTimers();
            marker.openPopup();
          });
          marker.on('mouseout', () => scheduleClose());

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

      const btn = container.querySelector('.nav-to-gmaps-btn') as HTMLElement | null;
      if (!btn) return;

      const placeId = Number(btn.getAttribute('data-place-id'));
      if (!placeId) return;

      btn.addEventListener('click', (ev: Event) => {
        ev.stopPropagation();
        this.handleNavigationClick(placeId);
      });
    };
    this.map.on('popupopen', this.popupOpenHandler);
  }
}
