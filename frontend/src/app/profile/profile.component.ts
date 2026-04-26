import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService, CurrentUser } from '../core/auth.service';
import { UserStateService } from '../core/user-state.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: CurrentUser | null = null;
  initials = '?';
  displayName = 'Użytkownik';
  loading = true;

  constructor(
    private readonly userState: UserStateService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.userState.loadUser().subscribe({
      next: (user) => {
        this.user = user;
        this.initials = this.userState.getInitials(user);
        this.displayName = this.userState.getDisplayName(user);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  get memberSince(): string {
    return 'SGGW Student';
  }

  logout(): void {
    this.authService.logout();
    this.userState.clear();
    this.router.navigate(['/login']);
  }
}
