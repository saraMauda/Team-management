import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { UsersService } from '../../../../../services/users.service';
import { TeamService } from '../../../../../services/team.service';

import { UsersDTO } from '../../../../../models/users-dto.model';
import { TeamDTO } from '../../../../../models/team-dto.model';

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

  addTouched = false;
  editTouched = false;
  
  newUser = {
    name: '',
    email: '',
    password: '',
    role: 'ROLE_EMPLOYEE',
    active: true
  };

  editingUser: UsersDTO | null = null;
  editingImageFile: File | null = null;
  previewImageBase64: string | null = null;

  teams: TeamDTO[] = [];
  teamLeaders: UsersDTO[] = [];
  teamEmployees: UsersDTO[] = [];

  expandedTeams: { [leaderId: number]: boolean } = {};
  selectedMemberToAdd: { [leaderId: number]: number | null } = {};

  /* ---------------------------------------------------------
     TOAST
  --------------------------------------------------------- */
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  showToast(message: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = message;
    this.toastType = type;

    setTimeout(() => {
      this.toastMessage = null;
    }, 5000);
  }

  /* ---------------------------------------------------------
     DELETE MODAL
  --------------------------------------------------------- */
  confirmDeleteId: number | null = null;

  openDeleteConfirm(id: number) {
    this.confirmDeleteId = id;
  }

  closeDeleteConfirm() {
    this.confirmDeleteId = null;
  }

  constructor(
    private usersService: UsersService,
    private teamService: TeamService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadTeams();
  }

  /* ---------------------------------------------------------
     LOAD USERS
  --------------------------------------------------------- */
  loadUsers(): void {
    this.loading = true;
    this.usersService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.refreshRoleLists();
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams = data || [];
      },
      error: () => console.error('Failed to load teams')
    });
  }

  refreshRoleLists(): void {
    this.teamLeaders = this.users.filter(u => this.isTeamLeader(u));
    this.teamEmployees = this.users.filter(u => this.isEmployee(u));
  }

  /* ---------------------------------------------------------
     VALIDATION — ADD USER
  --------------------------------------------------------- */

  isAddNameInvalid(): boolean {
    if (!this.newUser.name) return true;

    const parts = this.newUser.name.trim().split(' ');

    return (
      parts.length < 2 ||
      parts[0].length < 2 ||
      parts[1].length < 2
    );
  }

  isAddEmailInvalid(): boolean {
    if (!this.newUser.email) return true;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const endsCorrect = this.newUser.email.trim().toLowerCase().endsWith('@teamflow.com');

    return (!emailRegex.test(this.newUser.email.trim()) || !endsCorrect);
  }

  isAddUserFormValid(): boolean {
    const nameValid = !this.isAddNameInvalid();
    const emailValid = !this.isAddEmailInvalid();
    const passwordValid =
      !this.newUser.password || this.newUser.password.length >= 4;
    const roleValid = !!this.newUser.role;

    return nameValid && emailValid && passwordValid && roleValid;
  }

  /* ---------------------------------------------------------
     ADD USER
  --------------------------------------------------------- */
  addUser(): void {
    if (!this.isAddUserFormValid()) {
      this.showToast("Invalid user data. Please check all fields.", "error");
      return;
    }

    const payload: Partial<UsersDTO> = {
      name: this.newUser.name.trim(),
      email: this.newUser.email.trim(),
      password: this.newUser.password || '1234',
      role: this.newUser.role,
      active: this.newUser.active
    };

    this.saving = true;

    this.usersService.create(payload).subscribe({
      next: (user) => {
        this.users.unshift(user);
        this.refreshRoleLists();
        this.resetAddForm();
        this.showToast('User added successfully!', 'success');
        this.saving = false;
      },
      error: () => {
        this.showToast('Failed to add user.', 'error');
        this.saving = false;
      }
    });
  }

  resetAddForm(): void {
    this.newUser = {
      name: '',
      email: '',
      password: '',
      role: 'ROLE_EMPLOYEE',
      active: true
    };
    this.showAddForm = false;
  }

  /* ---------------------------------------------------------
     VALIDATION — EDIT USER
  --------------------------------------------------------- */

  isEditNameInvalid(): boolean {
    if (!this.editingUser?.name) return true;

    const parts = this.editingUser.name.trim().split(' ');

    return (
      parts.length < 2 ||
      parts[0].length < 2 ||
      parts[1].length < 2
    );
  }

  isEditEmailInvalid(): boolean {
    if (!this.editingUser?.email) return true;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const endsCorrect = this.editingUser.email.trim().toLowerCase().endsWith('@teamflow.com');

    return (!emailRegex.test(this.editingUser.email.trim()) || !endsCorrect);
  }

  isEditFormValid(): boolean {
    if (!this.editingUser) return false;

    const nameValid = !this.isEditNameInvalid();
    const emailValid = !this.isEditEmailInvalid();
    const roleValid = !!this.editingUser.role;

    return nameValid && emailValid && roleValid;
  }

  /* ---------------------------------------------------------
     EDIT USER
  --------------------------------------------------------- */
  openEdit(user: UsersDTO): void {
    this.editingUser = { ...user };
    this.previewImageBase64 = user.image || null;
    this.showEditForm = true;
  }

  onEditImageSelected(ev: Event): void {
    const input = ev.target as HTMLInputElement;
    if (!input.files?.length) return;

    this.editingImageFile = input.files[0];

    const reader = new FileReader();
    reader.onload = () => (this.previewImageBase64 = reader.result as string);
    reader.readAsDataURL(this.editingImageFile);
  }

  updateUser(): void {
    if (!this.editingUser?.id || !this.isEditFormValid()) {
      this.showToast("Invalid user data.", "error");
      return;
    }

    this.saving = true;
    const id = this.editingUser.id;
    const payload: Partial<UsersDTO> = { ...this.editingUser };

    const finalize = () => {
      this.usersService.update(id, payload).subscribe({
        next: (updated) => {
          this.users = this.users.map(u => u.id === updated.id ? updated : u);
          this.refreshRoleLists();
          this.cancelEdit();
          this.showToast('User updated successfully!', 'success');
          this.saving = false;
        },
        error: () => {
          this.showToast('Failed to update user.', 'error');
          this.saving = false;
        }
      });
    };

    if (this.editingImageFile) {
      this.usersService.uploadImage(id, this.editingImageFile).subscribe({
        next: (base64) => {
          payload.image = base64;
          finalize();
        },
        error: () => {
          this.showToast('Failed to upload image.', 'error');
          this.saving = false;
        }
      });
    } else {
      finalize();
    }
  }

  /* ---------------------------------------------------------
     DELETE USER
  --------------------------------------------------------- */
  deleteUserConfirmed() {
    if (this.confirmDeleteId === null) return;

    const id = this.confirmDeleteId;

    this.usersService.delete(id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);

        this.teams.forEach(team => {
          team.members = team.members.filter(m => m.id !== id);
        });

        this.refreshRoleLists();
        this.showToast('User deleted successfully!', 'success');
        this.confirmDeleteId = null;
      },
      error: () => {
        this.showToast('Failed to delete user.', 'error');
        this.confirmDeleteId = null;
      }
    });
  }

  cancelEdit(): void {
    this.showEditForm = false;
    this.editingUser = null;
    this.previewImageBase64 = null;
    this.editingImageFile = null;
  }

  /* ---------------------------------------------------------
     HELPERS
  --------------------------------------------------------- */
  isTeamLeader(u: UsersDTO): boolean {
    return u.role?.includes('TEAMLEADER') ?? false;
  }

  isEmployee(u: UsersDTO): boolean {
    return u.role?.includes('EMPLOYEE') ?? false;
  }

  prettyRole(role: string): string {
    const map: any = {
      'ROLE_ADMIN': 'Admin',
      'ROLE_TEAMLEADER': 'Team Leader',
      'ROLE_EMPLOYEE': 'Employee'
    };
    return map[role] || role;
  }

  getTeamByLeader(leaderId: number): TeamDTO | null {
    return this.teams.find(t => t.leaderId === leaderId) || null;
  }

  isTeamExpanded(leaderId: number): boolean {
    return !!this.expandedTeams[leaderId];
  }

  toggleTeam(leaderId: number): void {
    const team = this.getTeamByLeader(leaderId);

    if (!team) {
      this.teamService.createTeam(leaderId, []).subscribe({
        next: newTeam => {
          this.teams.push(newTeam);
          this.expandedTeams[leaderId] = true;
        },
        error: () => this.showToast('Failed to create team.', 'error')
      });
    } else {
      this.expandedTeams[leaderId] = !this.expandedTeams[leaderId];
    }
  }

  getTeamMembers(leaderId: number): UsersDTO[] {
    return this.getTeamByLeader(leaderId)?.members || [];
  }

  isMemberInTeam(leaderId: number, userId: number): boolean {
    const team = this.getTeamByLeader(leaderId);
    return !!team?.members.some(m => m.id === userId);
  }

  addMemberToLeader(leaderId: number): void {
    const memberId = this.selectedMemberToAdd[leaderId];
    if (memberId == null) return;

    const team = this.getTeamByLeader(leaderId);
    if (!team) return;

    this.teamService.addMember(team.id, memberId).subscribe({
      next: updated => {
        this.teams = this.teams.map(t => t.id === updated.id ? updated : t);
        this.selectedMemberToAdd[leaderId] = null;
        this.showToast('Employee added to team!', 'success');
      },
      error: () => this.showToast('Failed to add member.', 'error')
    });
  }

  removeMemberFromLeader(leaderId: number, memberId: number): void {
    const team = this.getTeamByLeader(leaderId);
    if (!team) return;

    this.teamService.removeMember(memberId).subscribe({
      next: () => {
        team.members = team.members.filter(m => m.id !== memberId);
        this.showToast('Member removed.', 'success');
      },
      error: () => this.showToast('Failed to remove member.', 'error')
    });
  }
markAddTouched() {
  this.addTouched = true;
}

markEditTouched() {
  this.editTouched = true;
}
}
