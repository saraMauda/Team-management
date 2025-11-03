import { Routes } from '@angular/router';
import { AdminDashboardComponent } from './components/dashboard/admin/admin-dashboard/admin-dashboard.component';
import { OverviewComponent } from './components/dashboard/admin/overview/overview.component';
import { ManageUsersComponent } from './components/dashboard/admin/users/manage-users/manage-users.component';
import { ManageProjectsComponent } from './components/dashboard/admin/projects/manage-projects/manage-projects.component';
import { ManageReportsComponent } from './components/dashboard/admin/reports/manage-reports/manage-reports.component';
import { ManageMeetingsComponent } from './components/dashboard/admin/meetings/manage-meetings/manage-meetings.component';
import { SettingsComponent } from './components/dashboard/admin/settings/settings.component';

export const routes: Routes = [
  { path: '', redirectTo: 'admin-dashboard', pathMatch: 'full' },
  {
    path: 'admin-dashboard',
    component: AdminDashboardComponent,
        children: [
    { path: '', redirectTo: 'overview', pathMatch: 'full' },
    { path: 'overview', component: OverviewComponent },
      { path: 'users', component: ManageUsersComponent },
      { path: 'projects', component: ManageProjectsComponent },
      { path: 'reports', component: ManageReportsComponent },
      { path: 'meetings', component: ManageMeetingsComponent },
      { path: 'settings', component: SettingsComponent },
    ]
  }
];
