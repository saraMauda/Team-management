import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { HeaderComponent } from '../../../../shared/header/header.component';
import { FooterComponent } from '../../../../shared/footer/footer.component';

@Component({
  selector: 'app-leader-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    HeaderComponent,
    FooterComponent
  ],
  templateUrl: './leader-dashboard.component.html',
  styleUrls: ['./leader-dashboard.component.css']
})
export class LeaderDashboardComponent {}
