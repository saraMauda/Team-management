import { Component } from '@angular/core';
import { HeaderComponent } from '../../../../shared/header/header.component';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../../../../shared/footer/footer.component';

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [HeaderComponent,RouterOutlet,FooterComponent],  templateUrl: './employee-dashboard.component.html',
  styleUrl: './employee-dashboard.component.css'
})
export class EmployeeDashboardComponent {

}
