import { Component } from '@angular/core';
import { HeaderComponent } from '../../../../shared/header/header.component';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from '../../../../shared/footer/footer.component';
@Component({
  selector: 'app-leader-dashboard',
  standalone: true,
  imports: [HeaderComponent,RouterOutlet,FooterComponent],  templateUrl: './leader-dashboard.component.html',
  styleUrl: './leader-dashboard.component.css'
})
export class LeaderDashboardComponent {

}
