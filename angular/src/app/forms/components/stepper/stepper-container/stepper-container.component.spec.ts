import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepperContainerComponent } from './stepper-container.component';
import {RouterModule} from "@angular/router";

describe('StepperContainerComponent', () => {
  let component: StepperContainerComponent;
  let fixture: ComponentFixture<StepperContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
      ],
      declarations: [ StepperContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepperContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
