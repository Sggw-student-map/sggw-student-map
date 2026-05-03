import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UserStateService } from '../../core/user-state.service';
import { CurrentUser } from '../../core/auth.service';
import { TokenStorageService } from '../../core/token-storage.service';

interface NavbarMenuItem {
  name: string;
  route: string;
  dotClass: string;
  activeClass: string;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent implements OnInit {
  currentUser: CurrentUser | null = null;
  userInitials = '?';
  userDisplayName = 'Użytkownik';
  userMenuOpen = false;

  readonly menuItems: NavbarMenuItem[] = [
    { name: 'Feed',       route: '/feed',     dotClass: 'bg-sky-300',    activeClass: 'ring-sky-300/70' },
    { name: 'Wydarzenia', route: '/events',   dotClass: 'bg-green-300',  activeClass: 'ring-green-300/70' },
    { name: 'Opinie',     route: '/opinions', dotClass: 'bg-pink-300',   activeClass: 'ring-pink-300/70' },
    { name: 'Znajomi',    route: '/friends',  dotClass: 'bg-orange-300', activeClass: 'ring-orange-300/70' },
  ];

  constructor(
    private readonly userState: UserStateService,
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router,
    private readonly elementRef: ElementRef<HTMLElement>
  ) {}

  ngOnInit(): void {
    this.userState.loadUser().subscribe();
    this.userState.user$.subscribe((user: CurrentUser | null) => {
      this.currentUser = user;
      this.userInitials = this.userState.getInitials(user);
      this.userDisplayName = this.userState.getDisplayName(user);
    });
  }

  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
  }

  closeUserMenu(): void {
    this.userMenuOpen = false;
  }

  logout(): void {
    this.tokenStorage.clear();
    this.userState.clear();
    this.closeUserMenu();
    this.router.navigate(['/login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.userMenuOpen) {
      return;
    }
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.userMenuOpen = false;
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.userMenuOpen = false;
  }
}
