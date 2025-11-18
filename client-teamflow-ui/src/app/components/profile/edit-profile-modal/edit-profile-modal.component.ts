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

  constructor(private usersService: UsersService) {}

  ngOnInit() {
    this.name = this.user.name;
    this.email = this.user.email;
    this.imagePreview = this.user.image;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  save() {
    const updated = {
      ...this.user,
      name: this.name,
      email: this.email
    };

    this.usersService.update(this.user.id, updated).subscribe(() => {
      if (this.selectedFile) {
        this.usersService.uploadImage(this.user.id, this.selectedFile).subscribe(() => {
          this.close.emit();
        });
      } else {
        this.close.emit();
      }
    });
  }

  cancel() {
    this.close.emit();
  }
}
