// src/app/models/users-dto.model.ts

export type UserRole =
  | 'ROLE_ADMIN'
  | 'ROLE_TEAMLEADER'
  | 'ROLE_EMPLOYEE'
  | string;

export interface UsersDTO {
  id: number;
  name: string;
  email: string;
  password?: string;
  role: UserRole;        // חשוב – מחרוזת יחידה בלבד
  active: boolean;
  image?: string; 
  roleString:string       // Base64 או null
}
