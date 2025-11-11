import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeaderDashboardHomeComponent } from './leader-dashboard-home.component';

describe('LeaderDashboardHomeComponent', () => {
  let component: LeaderDashboardHomeComponent;
  let fixture: ComponentFixture<LeaderDashboardHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeaderDashboardHomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LeaderDashboardHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
