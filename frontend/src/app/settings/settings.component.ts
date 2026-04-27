import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SettingsService, UserSettingsResponse } from '../core/settings.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  privateAccount = false;

  saving = false;
  saveSuccess = false;

  constructor(private settingsService: SettingsService) {}

  ngOnInit(): void {
    this.loadSettings();
  }

  loadSettings(): void {
    this.settingsService.getSettings().subscribe({
      next: (data) => {
        this.privateAccount = data.privateAccount;
      },
      error: (err) => console.error('Błąd pobierania ustawień:', err)
    });
  }

  saveSettings(): void {
    this.saving = true;
    this.saveSuccess = false;
    this.settingsService.updateSettings({ privateAccount: this.privateAccount }).subscribe({
      next: () => {
        this.saving = false;
        this.saveSuccess = true;
        setTimeout(() => { this.saveSuccess = false; }, 3000);
      },
      error: (err) => {
        console.error('Błąd zapisu ustawień:', err);
        this.saving = false;
      }
    });
  }
}
