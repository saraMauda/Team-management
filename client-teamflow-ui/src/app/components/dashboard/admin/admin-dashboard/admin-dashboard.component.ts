import { Component } from '@angular/core';
import { HeaderComponent } from '../../../../shared/header/header.component';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../../../../shared/footer/footer.component';
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [HeaderComponent,RouterOutlet,FooterComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {

}
