// src/app/models/users-dto.model.ts
export type UserRole = 'ADMIN' | 'TEAM_LEADER' | 'EMPLOYEE' | string;

export interface UsersDTO {
  id: number;
  name: string;
  email: string;
  password?: string;      // בדרך כלל לא מחזירים ללקוח, אבל נשאיר אופציונלי
  role: UserRole;
  active: boolean;
  image?: string;         // Base64 שהמורה יצרה עם ImageUtils.getImage(...)
}
// src/app/models/users-dto.model.ts
export interface UsersDTO {
  id: number;
  name: string;
  email: string;
  password?: string;        // שדה אופציונלי (לא תמיד חוזר מהשרת)
  role: string;             // ADMIN / TEAM_LEADER / EMPLOYEE
  active: boolean;
  imageBase64?: string;     // התמונה עצמה כ־Base64 (מהשרת)
  imagePath?: string;       // הנתיב לקובץ (בשרת)
}
