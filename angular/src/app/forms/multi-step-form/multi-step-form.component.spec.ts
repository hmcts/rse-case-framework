import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiStepFormComponent } from './multi-step-form.component';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

describe('MultiStepFormComponent', () => {
  let component: MultiStepFormComponent;
  let fixture: ComponentFixture<MultiStepFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MultiStepFormComponent ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        RouterModule.forRoot([]),
      ],

    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiStepFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
