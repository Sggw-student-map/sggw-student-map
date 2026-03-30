import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements OnInit, AfterViewInit {
  private map!: L.Map;

  menuItems = [
    { name: 'Feed', color: 'bg-sky-200 text-sky-700' },
    { name: 'Wydarzenia', color: 'bg-green-200 text-green-700' },
    { name: 'Opinie', color: 'bg-pink-200 text-pink-700' },
    { name: 'Znajomi', color: 'bg-orange-200 text-orange-700' },
    { name: 'Powiadomienia', color: 'bg-yellow-200 text-yellow-700' }
  ];

  constructor() {}

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.initMap();
    this.addStaticMarkers();
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

  }

  private addStaticMarkers(): void {
    const blueIcon = L.icon({
      iconUrl: '/blue-mark.svg',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34]
    });

    const purpleIcon = L.icon({
      iconUrl: '/purple-mark.svg',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34]
    });

    L.marker([52.1625, 21.045], { icon: blueIcon }).addTo(this.map);
    L.marker([52.161, 21.049], { icon: blueIcon }).addTo(this.map);
    L.marker([52.163, 21.047], { icon: purpleIcon }).addTo(this.map).bindPopup('<b>Kawiarnia</b>');
  }
}
