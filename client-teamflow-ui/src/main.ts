import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Routes } from '@angular/router';
import { importProvidersFrom } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app/app.component';
import { AdminDashboardComponent } from './app/components/admin-dashboard/admin-dashboard.component';
export const routes: Routes = [
  { path: '', redirectTo: 'manage-users', pathMatch: 'full' },
  { path: 'admin-dashboard', component: AdminDashboardComponent }
];


bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),
    importProvidersFrom(FormsModule),
    provideRouter(routes)
  ]
}).catch(err => console.error(err));
