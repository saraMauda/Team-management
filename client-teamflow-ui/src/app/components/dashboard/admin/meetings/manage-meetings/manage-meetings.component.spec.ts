import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageMeetingsComponent } from './manage-meetings.component';

describe('ManageMeetingsComponent', () => {
  let component: ManageMeetingsComponent;
  let fixture: ComponentFixture<ManageMeetingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageMeetingsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageMeetingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
