import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepperStepComponent } from './stepper-step.component';

describe('StepperStepComponent', () => {
  let component: StepperStepComponent;
  let fixture: ComponentFixture<StepperStepComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StepperStepComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepperStepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
