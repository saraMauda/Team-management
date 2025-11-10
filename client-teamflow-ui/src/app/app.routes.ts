import { Routes } from '@angular/router';
import { AuthGuard } from './components/guards/auth.guard';

import { LoginComponent } from './components/auth/login/login.component';
import { AdminDashboardComponent } from './components/dashboard/admin/admin-dashboard/admin-dashboard.component';
import { OverviewComponent } from './components/dashboard/admin/overview/overview.component';
import { ManageReportsComponent } from './components/dashboard/admin/reports/manage-reports/manage-reports.component';
import { ManageMeetingsComponent } from './components/dashboard/admin/meetings/manage-meetings/manage-meetings.component';
import { ManageProjectsComponent } from './components/dashboard/admin/projects/manage-projects/manage-projects.component';
import { ManageUsersComponent } from './components/dashboard/admin/users/manage-users/manage-users.component';
import { LeaderDashboardComponent } from './components/dashboard/team-leader/leader-dashboard/leader-dashboard.component';


export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: 'admin-dashboard',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: OverviewComponent },
       { path: 'users', component: ManageUsersComponent },
       { path: 'projects', component: ManageProjectsComponent },
       { path: 'reports', component: ManageReportsComponent },
      { path: 'meetings', component: ManageMeetingsComponent },
    ],
  },

    

  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];
