import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { API_BASE_URL } from '../../../../../app.config';

@Component({
  selector: 'app-manage-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manage-reports.component.html',
  styleUrls: ['./manage-reports.component.css']
})
export class ManageReportsComponent implements OnInit {

  teams: any[] = [];
  expandedEmployee: number | null = null;
  employeeReports: any[] = [];
  selectedReport: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadTeams();
  }

  loadTeams() {
    this.http.get<any[]>(`${API_BASE_URL}/teams/all`, { withCredentials: true })
      .subscribe({
        next: (res) => {
          this.teams = res;
        },
        error: err => console.error(err)
      });
  }

  toggleEmployee(emp: any) {
    if (this.expandedEmployee === emp.id) {
      this.expandedEmployee = null;
      this.employeeReports = [];
      return;
    }

    this.expandedEmployee = emp.id;

    this.http.get<any[]>(`${API_BASE_URL}/reports/byEmployee/${emp.id}`, { withCredentials: true })
      .subscribe({
        next: (reports) => {
          this.employeeReports = reports;
        },
        error: err => console.error(err)
      });
  }

  openReport(r: any) {
    this.selectedReport = r;
  }

  closePanel() {
    this.selectedReport = null;
  }
}
