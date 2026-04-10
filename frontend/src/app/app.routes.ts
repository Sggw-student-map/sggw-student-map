import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Map } from './map/map';
import { Register } from './register/register';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'map', component: Map },
    { path: '**', redirectTo: 'login' }
];
