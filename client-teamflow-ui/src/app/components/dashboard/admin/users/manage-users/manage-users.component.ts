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

  /** ------- TEAMS (SERVER SIDE) ------- */
  teams: TeamDTO[] = [];

  teamLeaders: UsersDTO[] = [];
  teamEmployees: UsersDTO[] = [];

  expandedTeams: { [leaderId: number]: boolean } = {};

  selectedMemberToAdd: { [leaderId: number]: number | null } = {};

  constructor(
    private usersService: UsersService,
    private teamService: TeamService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadTeams();
  }

  /** -------------------------------
   *        LOAD USERS
   --------------------------------*/
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

  /** -------------------------------
   *        LOAD TEAMS
   --------------------------------*/
  loadTeams(): void {
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams = data || [];
      },
      error: (err) => {
        console.error('❌ Failed to load teams', err);
      }
    });
  }

  refreshRoleLists(): void {
    this.teamLeaders = this.users.filter(u => this.isTeamLeader(u));
    this.teamEmployees = this.users.filter(u => this.isEmployee(u));
  }

  /** -------------------------------
   *        ADD USER
   --------------------------------*/
  addUser(): void {
    if (!this.isAddUserFormValid()) {
      alert("❌ Invalid user data. Please check the fields.");
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
        this.saving = false;
      },
      error: () => {
        alert('❌ Failed to add user.');
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
  isEmailInvalid(): boolean {
    if (!this.newUser.email) return false;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return !emailRegex.test(this.newUser.email);
  }

  isAddUserFormValid(): boolean {
    const nameValid =
      !!this.newUser.name &&
      this.newUser.name.trim().length >= 3;

const emailValid = !this.isEmailInvalid();


    const passwordValid =
      !this.newUser.password || this.newUser.password.length >= 4;

    const roleValid =
      !!this.newUser.role;

    return nameValid && emailValid && passwordValid && roleValid;
  }


  /** -------------------------------
   *        EDIT USER
   --------------------------------*/
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
        next: (base64) => {
          payload.image = base64;
          finalize();
        },
        error: () => {
          alert('❌ Failed to upload image.');
          this.saving = false;
        }
      });
    } else {
      finalize();
    }
  }

  deleteUser(id: number): void {
    if (!confirm('Are you sure?')) return;

    this.usersService.delete(id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);

        // Remove from teams
        this.teams.forEach(team => {
          team.members = team.members.filter(m => m.id !== id);
        });

        this.refreshRoleLists();
      },
      error: () => alert('❌ Failed to delete user.')
    });
  }

  cancelEdit(): void {
    this.showEditForm = false;
    this.editingUser = null;
    this.previewImageBase64 = null;
    this.editingImageFile = null;
  }
isEditNameInvalid(): boolean {
  return !this.editingUser?.name || this.editingUser.name.trim().length < 3;
}

isEditEmailInvalid(): boolean {
  if (!this.editingUser?.email) return true;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return !emailRegex.test(this.editingUser.email.trim());
}

isEditRoleInvalid(): boolean {
  return !this.editingUser?.role;
}

isEditFormValid(): boolean {
  if (!this.editingUser) return false;

  const nameValid = !this.isEditNameInvalid();
  const emailValid = !this.isEditEmailInvalid();
  const roleValid = !this.isEditRoleInvalid();

  return nameValid && emailValid && roleValid;
}


  /** -------------------------------
   *        HELPER FUNCTIONS
   --------------------------------*/
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

  /** -------------------------------
   *        TEAMS LOGIC
   --------------------------------*/

  getTeamByLeader(leaderId: number): TeamDTO | null {
    return this.teams.find(t => t.leaderId === leaderId) || null;
  }

  isTeamExpanded(leaderId: number): boolean {
    return !!this.expandedTeams[leaderId];
  }

  toggleTeam(leaderId: number): void {
    const team = this.getTeamByLeader(leaderId);

    if (!team) {
      // Create empty team
      this.teamService.createTeam(leaderId, []).subscribe({
        next: newTeam => {
          this.teams.push(newTeam);
          this.expandedTeams[leaderId] = true;
        },
        error: () => alert('❌ Failed to create team.')
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

  /** ADD member */
  addMemberToLeader(leaderId: number): void {
    const memberId = this.selectedMemberToAdd[leaderId];
    if (memberId == null) return;

    const team = this.getTeamByLeader(leaderId);
    if (!team) return;

    this.teamService.addMember(team.id, memberId).subscribe({
      next: updated => {
        this.teams = this.teams.map(t => t.id === updated.id ? updated : t);
        this.selectedMemberToAdd[leaderId] = null;
      },
      error: () => alert('❌ Failed to add member.')
    });
  }

  /** REMOVE member */
  removeMemberFromLeader(leaderId: number, memberId: number): void {
    const team = this.getTeamByLeader(leaderId);
    if (!team) return;

    const teamMemberEntry = team.members.find(m => m.id === memberId);
    if (!teamMemberEntry) return;

    this.teamService.removeMember(memberId).subscribe({
      next: () => {
        team.members = team.members.filter(m => m.id !== memberId);
      },
      error: () => alert('❌ Failed to remove member.')
    });
  }
}
