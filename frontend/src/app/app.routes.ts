import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Map } from './map/map';
import { Register } from './register/register';
import { OpinionsPageComponent } from './reviews/opinions-page.component';
import { FriendsComponent } from './friends/friends.component';
import { ProfileComponent } from './profile/profile.component';
import { Notifications } from './notifications/notifications';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'map', component: Map },
    { path: 'opinions', component: OpinionsPageComponent },
    { path: 'friends', component: FriendsComponent },
    { path: 'profile', component: ProfileComponent },
    { path: 'notifications', component: Notifications },
    { path: '**', redirectTo: 'login' }
];
