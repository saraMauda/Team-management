import { Routes } from '@angular/router';
import { AuthGuard } from './components/guards/auth.guard';

// Auth
import { LoginComponent } from './components/auth/login/login.component';

// Admin
import { AdminDashboardComponent } from './components/dashboard/admin/admin-dashboard/admin-dashboard.component';
import { OverviewComponent } from './components/dashboard/admin/overview/overview.component';
import { ManageReportsComponent } from './components/dashboard/admin/reports/manage-reports/manage-reports.component';
import { ManageMeetingsComponent } from './components/dashboard/admin/meetings/manage-meetings/manage-meetings.component';
import { ManageProjectsComponent } from './components/dashboard/admin/projects/manage-projects/manage-projects.component';
import { ManageUsersComponent } from './components/dashboard/admin/users/manage-users/manage-users.component';

// Team Leader
import { LeaderDashboardComponent } from './components/dashboard/team-leader/leader-dashboard/leader-dashboard.component';
import { LeaderDashboardHomeComponent } from './components/dashboard/team-leader/leader-dashboard-home/leader-dashboard-home.component';
import { FeedbackComponent } from './components/dashboard/team-leader/feedback/feedback.component';
import { TeamProjectsComponent } from './components/dashboard/team-leader/team-projects/team-projects.component';
import { TeamMeetingsComponent } from './components/dashboard/team-leader/team-meetings/team-meetings.component';
import { TeamReportsComponent } from './components/dashboard/team-leader/team-reports/team-reports.component';

// Employee
import { EmployeeDashboardComponent } from './components/dashboard/employee/employee-dashboard/employee-dashboard.component';
import { MyProjectsComponent } from './components/dashboard/employee/my-projects/my-projects.component';
import { MeetingsComponent } from './components/dashboard/employee/meetings/meetings.component';
import { MyReportsComponent } from './components/dashboard/employee/my-reports/my-reports.component';
import { AddReportComponent } from './components/dashboard/employee/add-report/add-report.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  // 🟧 Admin Dashboard
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

  // 🟦 Team Leader Dashboard
 {
  path: 'leader-dashboard',
  component: LeaderDashboardComponent,
  canActivate: [AuthGuard],
  children: [
    { path: '', component: LeaderDashboardHomeComponent }, // Home
    { path: 'team-projects', component: TeamProjectsComponent },
    { path: 'team-meetings', component: TeamMeetingsComponent },
    { path: 'team-reports', component: TeamReportsComponent },
    { path: 'feedback', component: FeedbackComponent },
  ],
},

  // 🟩 Employee Dashboard
  {
    path: 'employee-dashboard',
    component: EmployeeDashboardComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'my-projects', pathMatch: 'full' },
      { path: 'my-projects', component: MyProjectsComponent },
      { path: 'meetings', component: MeetingsComponent },
      { path: 'my-reports', component: MyReportsComponent },
      { path: 'add-report', component: AddReportComponent },
    ],
  },

  // Default routes
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' },
];
