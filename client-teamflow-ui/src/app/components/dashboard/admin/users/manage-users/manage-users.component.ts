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

  newUser: Partial<UsersDTO> = {
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
     TOAST SYSTEM
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
     DELETE CONFIRMATION MODAL
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
  ) {}

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

  /* ---------------------------------------------------------
     LOAD TEAMS
  --------------------------------------------------------- */
  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams = data || [];
      },
      error: () => {
        console.error('Failed to load teams');
      }
    });
  }

  refreshRoleLists(): void {
    this.teamLeaders = this.users.filter(u => this.isTeamLeader(u));
    this.teamEmployees = this.users.filter(u => this.isEmployee(u));
  }

  /* ---------------------------------------------------------
     ADD USER
  --------------------------------------------------------- */
  addUser(): void {
    if (!this.newUser.name || !this.newUser.email) return;

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
    if (!this.editingUser?.id) return;

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
     DELETE USER (WITH CUSTOM CONFIRM MODAL)
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

  closeModal() {
    this.confirmDeleteId = null;
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
}
