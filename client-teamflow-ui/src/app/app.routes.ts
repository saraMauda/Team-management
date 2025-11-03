import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // 🔐 AUTH
  {
    path: 'login',
    loadComponent: () =>
      import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },

  // 🧑‍💼 ADMIN
  {
    path: 'admin',
    loadComponent: () =>
      import('./components/dashboard/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      {
        path: 'overview',
        loadComponent: () =>
          import('./components/dashboard/admin/overview/overview.component').then(m => m.OverviewComponent)
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./components/dashboard/admin/users/manage-users/manage-users.component').then(m => m.ManageUsersComponent)
      },
      {
        path: 'projects',
        loadComponent: () =>
          import('./components/dashboard/admin/projects/manage-projects/manage-projects.component').then(m => m.ManageProjectsComponent)
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./components/dashboard/admin/reports/manage-reports/manage-reports.component').then(m => m.ManageReportsComponent)
      },
      {
        path: 'meetings',
        loadComponent: () =>
          import('./components/dashboard/admin/meetings/manage-meetings/manage-meetings.component').then(m => m.ManageMeetingsComponent)
      },
      {
        path: 'settings',
        loadComponent: () =>
          import('./components/dashboard/admin/settings/settings.component').then(m => m.SettingsComponent)
      }
    ]
  },

  // 👨‍🏫 TEAM LEADER
  {
    path: 'leader',
    loadComponent: () =>
      import('./components/dashboard/team-leader/leader-dashboard/leader-dashboard.component').then(m => m.LeaderDashboardComponent),
    children: [
      { path: '', redirectTo: 'team-projects', pathMatch: 'full' },
      {
        path: 'team-projects',
        loadComponent: () =>
          import('./components/dashboard/team-leader/team-projects/team-projects.component').then(m => m.TeamProjectsComponent)
      },
      {
        path: 'team-reports',
        loadComponent: () =>
          import('./components/dashboard/team-leader/team-reports/team-reports.component').then(m => m.TeamReportsComponent)
      },
      {
        path: 'team-meetings',
        loadComponent: () =>
          import('./components/dashboard/team-leader/team-meetings/team-meetings.component').then(m => m.TeamMeetingsComponent)
      },
      {
        path: 'feedback',
        loadComponent: () =>
          import('./components/dashboard/team-leader/feedback/feedback.component').then(m => m.FeedbackComponent)
      }
    ]
  },

  // 👷 EMPLOYEE
  {
    path: 'employee',
    loadComponent: () =>
      import('./components/dashboard/employee/employee-dashboard/employee-dashboard.component').then(m => m.EmployeeDashboardComponent),
    children: [
      { path: '', redirectTo: 'my-projects', pathMatch: 'full' },
      {
        path: 'my-projects',
        loadComponent: () =>
          import('./components/dashboard/employee/my-projects/my-projects.component').then(m => m.MyProjectsComponent)
      },
      {
        path: 'my-reports',
        loadComponent: () =>
          import('./components/dashboard/employee/my-reports/my-reports.component').then(m => m.MyReportsComponent)
      },
      {
        path: 'meetings',
        loadComponent: () =>
          import('./components/dashboard/employee/meetings/meetings.component').then(m => m.MeetingsComponent)
      },
      {
        path: 'add-report',
        loadComponent: () =>
          import('./components/dashboard/employee/add-report/add-report.component').then(m => m.AddReportComponent)
      }
    ]
  },

  // ❌ Not Found
  {
    path: '**',
    loadComponent: () =>
      import('./components/not-found/not-found.component').then(m => m.NotFoundComponent)
  }
];
