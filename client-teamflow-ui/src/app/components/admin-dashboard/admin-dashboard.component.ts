import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

type Role = 'ADMIN' | 'TEAM_LEADER' | 'EMPLOYEE';

interface UserRow {
  id: number;
  name: string;
  email: string;
  role: Role;
  active: boolean;
  imageBase64?: string; // יגיע מה-DTO שלך (UsersDTO.imageBase64)
}

interface ProjectItem {
  id: number;
  name: string;
  status: 'Active' | 'Completed' | 'On Hold';
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {
  // header
  appTitle = 'Admin Dashboard';
  adminName = 'Sara Mauda';
  adminImage = 'assets/profile-admin.png';     // קובץ דמה – או imageBase64 אמיתי מהשרת
  logoPath  = 'assets/logo.png';               // שימי כאן את הלוגו הכתום השקוף

  // stat cards (אפשר להחליף בהמשך לנתונים אמיתיים מה-API)
  stats = [
    { title: 'Total Users',    value: 250, icon: '👤' },
    { title: 'Active Projects', value: 15, icon: '✅' },
    { title: 'Reports Submitted', value: 120, icon: '🧾' },
  ];

  // Manage Projects (צד ימין)
  projects: ProjectItem[] = [
    { id: 1, name: 'Website Redesign',       status: 'Active' },
    { id: 2, name: 'Mobile App Development', status: 'Completed' },
    { id: 3, name: 'Marketing Campaign',     status: 'On Hold' }
  ];

  // Manage Users (טבלה תחת הגרף)
  users: UserRow[] = [
    {
      id: 1,
      name: 'John Doe',
      email: 'john@teamsample.com',
      role: 'EMPLOYEE',
      active: true,
      imageBase64: '' // כאן תכניסי Base64 שמגיע מה-API; אם ריק—נשתמש באווטאר ברירת מחדל ב-HTML
    },
    {
      id: 2,
      name: 'Jane Smith',
      email: 'jane@teamsample.com',
      role: 'EMPLOYEE',
      active: true
    },
    {
      id: 3,
      name: 'Alice Johnson',
      email: 'alice@teamsample.com',
      role: 'TEAM_LEADER',
      active: true
    },
    {
      id: 4,
      name: 'Bob Brown',
      email: 'bob@teamsample.com',
      role: 'TEAM_LEADER',
      active: false
    }
  ];

  // פעולות (מחוברות בהמשך ל-API)
  onEditUser(u: UserRow) { console.log('Edit user', u.id); }
  onDeleteUser(u: UserRow) { console.log('Delete user', u.id); }
  onManagePermissions() { console.log('Manage Permissions'); }
  onManageCategories() { console.log('Manage Categories'); }
  onBackupRestore() { console.log('Backup & Restore'); }

  projectStatusClass(s: ProjectItem['status']) {
    return {
      'status-badge': true,
      'active': s === 'Active',
      'completed': s === 'Completed',
      'onhold': s === 'On Hold'
    };
  }
}
