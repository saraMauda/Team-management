import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UsersService } from '../../services/users.service';
import { UsersDTO } from '../../models/users-dto.model';
import { EditProfileModalComponent } from '../../components/profile/edit-profile-modal/edit-profile-modal.component';
import { ChangePasswordModalComponent } from '../../components/profile/change-password-modal/change-password-modal.component';
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule,EditProfileModalComponent,
  ChangePasswordModalComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  menuOpen = false;
  currentUser: UsersDTO | null = null;

  userImage: string | null = null;   // base64
  userInitial: string = "?";

  constructor(
    private auth: AuthService,
    private usersService: UsersService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  // ✔ טוען את המשתמש המחובר לפי האימייל שנשמר ב-localStorage
  loadUser() {
    const storage = localStorage.getItem('user');
    if (!storage) return;

    const email = JSON.parse(storage).email;

    this.usersService.getByEmail(email).subscribe((user: UsersDTO) => {
      this.currentUser = user;

      // אות ראשונה לשם
      this.userInitial = user.name
        ? user.name.charAt(0).toUpperCase()
        : "?";

      // תמונת פרופיל מ-base64 (כמו בניהול משתמשים)
      if (user.image) {
        this.userImage = user.image;
      }
    });
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  // סגירת התפריט בלחיצה מחוץ
@HostListener('document:click', ['$event'])
closeOnOutsideClick(event: Event) {
  const target = event.target as HTMLElement;

  // ❗ אל תסגור אם זה מודל
  if (target.closest('.modal') || target.closest('.modal-backdrop')) {
    return;
  }

  if (!target.closest('.profile-wrapper') &&
      !target.closest('.profile-menu')) {
    this.menuOpen = false;
  }
}


  // Logout
  onLogout() {
    this.menuOpen = false;
    this.auth.signOut();
  }
  // מצבים להצגת המודלים
showEditProfile = false;
showChangePassword = false;

openProfile() {
  this.menuOpen = false;
  this.showEditProfile = true;
}

openChangePassword() {
  this.menuOpen = false;
  this.showChangePassword = true;
}

closeModals() {
  this.showEditProfile = false;
  this.showChangePassword = false;
}

}
