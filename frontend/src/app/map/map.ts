import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import { PlacePin, PlaceService } from '../core/place.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements OnInit, AfterViewInit {
  private map!: L.Map;
  private markerLayer = L.layerGroup();

  menuItems = [
    { name: 'Feed', color: 'bg-sky-200 text-sky-700' },
    { name: 'Wydarzenia', color: 'bg-green-200 text-green-700' },
    { name: 'Opinie', color: 'bg-pink-200 text-pink-700' ,route: '/opinions'},
    { name: 'Znajomi', color: 'bg-orange-200 text-orange-700' },
    { name: 'Powiadomienia', color: 'bg-yellow-200 text-yellow-700' }
  ];

  constructor(private readonly placeService: PlaceService) {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.initMap();
    this.loadPins();
  }

  private loadPins(): void {
    const blueIcon = L.icon({
      iconUrl: '/blue-mark.svg',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34]
    });

    this.placeService.getPins().subscribe({
      next: (pins: PlacePin[]) => {
        this.markerLayer.clearLayers();
        pins.forEach((pin) => {
          L.marker([pin.latitude, pin.longitude], { icon: blueIcon })
            .bindPopup(`<b>${pin.name}</b>`)
            .addTo(this.markerLayer);
        });
      },
      error: () => {
        this.markerLayer.clearLayers();
      }
    });
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
      touchZoom: false
    });

    L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-E nomenclature',
      maxZoom: 18
    }).addTo(this.map);

    this.markerLayer.addTo(this.map);
  }
}
