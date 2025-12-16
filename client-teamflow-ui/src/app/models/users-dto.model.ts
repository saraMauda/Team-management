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
  role: UserRole;       
  active: boolean;
  image?: string;
  roleString?: string; 
}
