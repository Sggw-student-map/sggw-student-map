import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { PlacePin, PlaceService, CreatePlaceRequest, NavigationResponse } from '../core/place.service';
import { UserStateService } from '../core/user-state.service';
import { CurrentUser } from '../core/auth.service';
import { RouterModule } from '@angular/router';

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

  addPlaceMode = false;
  showPlaceForm = false;
  newPlace: CreatePlaceRequest = { name: '', latitude: 0, longitude: 0, description: '' };
  private tempMarker?: L.Marker;
  formError = '';

  currentUser: CurrentUser | null = null;
  userInitials = '?';
  userDisplayName = 'Użytkownik';

  menuItems = [
    { name: 'Feed', color: 'bg-sky-200 text-sky-700' },
    { name: 'Wydarzenia', color: 'bg-green-200 text-green-700' },
    { name: 'Opinie', color: 'bg-pink-200 text-pink-700', route: '/opinions' },
    { name: 'Znajomi', color: 'bg-orange-200 text-orange-700', route: '/friends' },
    { name: 'Powiadomienia', color: 'bg-yellow-200 text-yellow-700' },
  ];

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
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.loadPins();
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.off('popupopen', this.popupOpenHandler);
      this.map.remove();
    }
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

  private removeTempMarker(): void {
    if (this.tempMarker) {
      this.map.removeLayer(this.tempMarker);
      this.tempMarker = undefined;
    }
  }

  private buildPopupHtml(pin: PlacePin): string {
    const name = this.escapeHtml(pin.name);
    const desc = pin.description ? this.escapeHtml(pin.description) : '';

    return `
      <div style="min-width:180px;font-family:system-ui,sans-serif">
        <div style="font-weight:700;font-size:14px;margin-bottom:4px">${name}</div>
        ${desc ? `<div style="color:#555;font-size:12px;margin-bottom:8px">${desc}</div>` : ''}
        <button
          class="nav-to-gmaps-btn"
          data-place-id="${pin.id}"
          style="
            display:flex;align-items:center;gap:6px;
            width:100%;padding:7px 12px;
            background:#1a73e8;color:#fff;
            border:none;border-radius:8px;
            font-size:12px;font-weight:600;
            cursor:pointer;transition:background .15s;
          "
          onmouseover="this.style.background='#1558b0'"
          onmouseout="this.style.background='#1a73e8'"
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
    const blueIcon = L.icon({
      iconUrl: '/blue-mark.svg',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
    });

    this.placeService.getAll().subscribe({
      next: (pins: PlacePin[]) => {
        this.markerLayer.clearLayers();
        pins.forEach((pin) => {
          L.marker([pin.latitude, pin.longitude], { icon: blueIcon })
            .bindPopup(this.buildPopupHtml(pin))
            .addTo(this.markerLayer);
        });
      },
      error: () => {
        this.markerLayer.clearLayers();
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
      dragging: false,
      keyboard: false,
      scrollWheelZoom: false,
      doubleClickZoom: false,
      boxZoom: false,
      touchZoom: false,
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
