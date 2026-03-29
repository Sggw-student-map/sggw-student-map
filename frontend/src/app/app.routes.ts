import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Map } from './map/map';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'map', component: Map },
    { path: '**', redirectTo: 'login' }
];
