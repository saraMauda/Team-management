import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeDashboardHomeComponent } from './employee-dashboard-home.component';

describe('EmployeeDashboardHomeComponent', () => {
  let component: EmployeeDashboardHomeComponent;
  let fixture: ComponentFixture<EmployeeDashboardHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeeDashboardHomeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeeDashboardHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
