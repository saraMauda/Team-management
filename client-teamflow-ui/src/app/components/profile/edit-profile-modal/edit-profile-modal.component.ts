import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsersDTO } from '../../../models/users-dto.model';
import { UsersService } from '../../../services/users.service';

@Component({
  selector: 'app-edit-profile-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile-modal.component.html',
  styleUrls: ['./edit-profile-modal.component.css']
})
export class EditProfileModalComponent {

  @Input() user!: UsersDTO;
  @Output() close = new EventEmitter<void>();

  name = '';
  email = '';
  imagePreview?: string | null = null;
  selectedFile: File | null = null;

  toastMessage = '';
  toastType: 'success' | 'error' | null = null;

  constructor(private usersService: UsersService) {}

  ngOnInit() {
    this.name = this.user.name;
    this.email = this.user.email;
    this.imagePreview = this.user.image ?? null;
  }

  /* ------- TOAST ------- */
  showToast(type: 'success' | 'error', msg: string) {
    this.toastType = type;
    this.toastMessage = msg;

    setTimeout(() => {
      this.toastType = null;
      this.toastMessage = '';
    }, 5000);
  }

  /* ------- IMAGE SELECT ------- */
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = () => this.imagePreview = reader.result as string;
    reader.readAsDataURL(file);
  }

  /* ------- SAVE ------- */
  save() {
    // Name validation
    if (!this.name || this.name.trim().length < 2) {
      this.showToast('error', "Name is too short");
      return;
    }

    // Email required
    if (!this.email || this.email.trim().length === 0) {
      this.showToast('error', "Email is required");
      return;
    }

    // Company email validation
    const companyEmailRegex = /^[A-Za-z0-9._%+-]+@teamflow\.com$/;
    if (!companyEmailRegex.test(this.email)) {
      this.showToast('error', "Email must end with @teamflow.com");
      return;
    }

    const updated: Partial<UsersDTO> = {
      name: this.name,
      email: this.email
    };

    this.usersService.update(this.user.id, updated).subscribe({
      next: () => {
        if (this.selectedFile) {
          this.usersService.uploadImage(this.user.id, this.selectedFile).subscribe({
            next: () => {
              this.onSuccess();
            },
            error: () => this.showToast('error', 'Image upload failed')
          });
        } else {
          this.onSuccess();
        }
      },
      error: () => this.showToast('error', 'Update failed')
    });
  }

  /* ------- SUCCESS HANDLER ------- */
  private onSuccess() {
    this.showToast('success', 'Profile updated successfully');

    setTimeout(() => {
      window.location.reload();
      this.close.emit();        
    }, 800);
  }

  cancel() {
    this.close.emit();
  }
}
