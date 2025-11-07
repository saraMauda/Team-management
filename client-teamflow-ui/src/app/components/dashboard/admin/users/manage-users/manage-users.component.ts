import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsersService } from '../../../../../services/users.service';
import { UsersDTO } from '../../../../../models/users-dto.model';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css']
})
export class ManageUsersComponent implements OnInit {
  users: UsersDTO[] = [];
  loading = false;
  error: string | null = null;

  showAddForm = false;
  showEditForm = false;
  saving = false;

  newUser: Partial<UsersDTO> = {
    name: '',
    email: '',
    password: '',
    roleString: 'ROLE_EMPLOYEE',
    active: true
  };

  editingUser: UsersDTO | null = null;
  editingImageFile: File | null = null;
  previewImageBase64: string | null = null;

  constructor(private usersService: UsersService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.usersService.getAll().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  // ➕ יצירת משתמש חדש
  addUser(): void {
    if (!this.newUser.name || !this.newUser.email || !this.newUser.roleString) return;

    const payload: Partial<UsersDTO> = {
      name: this.newUser.name.trim(),
      email: this.newUser.email.trim(),
      password: this.newUser.password || '1234',
      roleString: this.newUser.roleString,
      active: this.newUser.active ?? true
    };

    this.saving = true;
    this.usersService.create(payload).subscribe({
      next: (user) => {
        this.users.unshift(user);
        this.newUser = {
          name: '',
          email: '',
          password: '',
          roleString: 'ROLE_EMPLOYEE',
          active: true
        };
        this.showAddForm = false;
        this.saving = false;
      },
      error: () => {
        alert('❌ Failed to add user.');
        this.saving = false;
      }
    });
  }

  // ✏️ פתיחת עריכה
  openEdit(user: UsersDTO): void {
    this.editingUser = { ...user };
    this.previewImageBase64 = user.image || null;
    this.showEditForm = true;
  }

  // 📸 בחירת תמונה לעריכה
  onEditImageSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.editingImageFile = input.files[0];

    const reader = new FileReader();
    reader.onload = () => (this.previewImageBase64 = reader.result as string);
    reader.readAsDataURL(this.editingImageFile);
  }

  // 💾 עדכון משתמש
  updateUser(): void {
    if (!this.editingUser?.id) return;
    this.saving = true;

    const id = this.editingUser.id;
    const payload: Partial<UsersDTO> = { ...this.editingUser };

    const finalizeUpdate = () => {
      this.usersService.update(id, payload).subscribe({
        next: (updatedUser) => {
          this.users = this.users.map(u => u.id === updatedUser.id ? updatedUser : u);
          this.cancelEdit();
          this.saving = false;
        },
        error: () => {
          alert('❌ Failed to update user.');
          this.saving = false;
        }
      });
    };

    if (this.editingImageFile) {
      this.usersService.uploadImage(id, this.editingImageFile).subscribe({
        next: (resultText) => {
          payload.image = resultText;
          finalizeUpdate();
        },
        error: () => {
          alert('❌ Failed to upload image.');
          this.saving = false;
        }
      });
    } else {
      finalizeUpdate();
    }
  }

  // ❌ מחיקת משתמש
  deleteUser(id: number): void {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.usersService.delete(id).subscribe({
      next: () => this.users = this.users.filter(u => u.id !== id),
      error: () => alert('❌ Failed to delete user.')
    });
  }

  cancelEdit(): void {
    this.showEditForm = false;
    this.editingUser = null;
    this.previewImageBase64 = null;
    this.editingImageFile = null;
  }

  // 🧾 הצגה נעימה של תפקיד
  prettyRole(role: string): string {
    const map: Record<string, string> = {
      'ROLE_ADMIN': 'Admin',
      'ROLE_TEAMLEADER': 'Team Leader',
      'ROLE_EMPLOYEE': 'Employee'
    };
    return map[role] || role;
  }
}
