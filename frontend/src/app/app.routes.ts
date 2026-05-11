import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Map } from './map/map';
import { Register } from './register/register';
import { OpinionsPageComponent } from './reviews/opinions-page.component';
import { FriendsComponent } from './friends/friends.component';
import { ProfileComponent } from './profile/profile.component';
import { FeedComponent } from './feed/feed.component';
import { EventsComponent } from './events/events.component';
import { SettingsComponent } from './settings/settings.component';
import { ApiDocsComponent } from './docs/api-docs.component';
import { authGuard } from './core/auth.guard';
import { guestGuard } from './core/guest.guard';
import { Verify } from './verify/verify';


export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login, canActivate: [guestGuard] },
    { path: 'register', component: Register, canActivate: [guestGuard] },
    { path: 'map', component: Map, canActivate: [authGuard] },
    { path: 'opinions', component: OpinionsPageComponent, canActivate: [authGuard] },
    { path: 'friends', component: FriendsComponent, canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
    { path: 'feed', component: FeedComponent, canActivate: [authGuard] },
    { path: 'events', component: EventsComponent, canActivate: [authGuard] },
    { path: 'settings', component: SettingsComponent, canActivate: [authGuard] },
    { path: 'docs', component: ApiDocsComponent },
    { path: 'verify', component: Verify},
    { path: '**', redirectTo: 'login' },
];
