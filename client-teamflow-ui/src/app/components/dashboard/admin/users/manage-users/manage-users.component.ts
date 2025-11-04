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

  // create
  showAddForm = false;
  newUser: Partial<UsersDTO> = { name: '', email: '', role: 'EMPLOYEE', active: true };

  // edit
  showEditForm = false;
  editingUser: Partial<UsersDTO> | null = null;
  editingImageFile: File | null = null;
  previewImageBase64: string | null = null;
  saving = false;

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

  // create
  addUser(): void {
    if (!this.newUser.name || !this.newUser.email) return;
    this.usersService.create(this.newUser).subscribe({
      next: (user) => {
        this.users.unshift(user);
        this.showAddForm = false;
        this.newUser = { name: '', email: '', role: 'EMPLOYEE', active: true };
      },
      error: () => alert('Failed to add user.')
    });
  }

  // open edit form with selected user
  openEdit(user: UsersDTO): void {
    this.editingUser = { ...user }; // clone so לא נשפיע על הטבלה לפני שמירה
    this.previewImageBase64 = user.image ?? null;
    this.editingImageFile = null;
    this.showEditForm = true;
  }

  // file picker change
  onEditImageSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    this.editingImageFile = input.files[0];

    // show preview
    const reader = new FileReader();
    reader.onload = () => {
      this.previewImageBase64 = reader.result as string;
    };
    reader.readAsDataURL(this.editingImageFile);
  }

  // update user + optional image upload
  updateUser(): void {
    if (!this.editingUser || !this.editingUser.id) return;
    this.saving = true;

    const finalizeUpdate = (updatedFields?: Partial<UsersDTO>) => {
      const id = this.editingUser!.id as number;
      const payload: Partial<UsersDTO> = { ...this.editingUser, ...updatedFields };
      // אם משתמש החליף סיסמה – זה נשלח ב payload.password (שרת -> חיבור אחראי)
      this.usersService.update(id, payload).subscribe({
        next: (updatedUser) => {
          // עדכון ברשימת המשתמשים בצד לקוחות
          this.users = this.users.map(u => u.id === updatedUser.id ? updatedUser : u);
          this.showEditForm = false;
          this.editingUser = null;
          this.editingImageFile = null;
          this.previewImageBase64 = null;
          this.saving = false;
        },
        error: () => {
          alert('Failed to update user.');
          this.saving = false;
        }
      });
    };

    // אם בוחרים תמונה חדשה – קודם נשלח אותה ל־endpoint של uploadImage ואז נקבל (server) שם קובץ או מחרוזת
    if (this.editingImageFile) {
      // משתמש מחליף תמונה
      this.usersService.uploadImage(this.editingUser.id!, this.editingImageFile).subscribe({
        next: (resultText) => {
          // המורה שלך מוחזר טקסט (למשל שם הקובץ או "ok"), אם נחוץ ניתן לשים את השם בשדה image לפני update
          // כאן نفترض שהשרת שומר את השם ב־imagePath ויש לך API שמחזיר את השם/url או שמחכה לבקשת ה־get לאחר שמירת השם.
          // אם ה־upload מחזיר את ה־base64 או path, התאם לפי השרת:
          // נכניס את הערך לשדה image ונמשיך ב־update
          (this.editingUser as any).image = resultText; // או נתאם לפי שרתך
          finalizeUpdate();
        },
        error: () => {
          alert('Failed to upload image.');
          this.saving = false;
        }
      });
    } else {
      // אין תמונה חדשה — רק שומרים את השדות
      finalizeUpdate();
    }
  }

  deleteUser(id: number): void {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.usersService.delete(id).subscribe({
      next: () => this.users = this.users.filter(u => u.id !== id),
      error: () => alert('Failed to delete user.')
    });
  }

  cancelEdit(): void {
    this.showEditForm = false;
    this.editingUser = null;
    this.previewImageBase64 = null;
    this.editingImageFile = null;
  }
}
