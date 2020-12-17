import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateInputComponent } from './date-input.component';
import {FormControl, FormGroup} from '@angular/forms';

describe('DateInputComponent', () => {
  let component: DateInputComponent;
  let fixture: ComponentFixture<DateInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DateInputComponent ]
    })
    .compileComponents();
  });

  const dob = new FormControl();
  beforeEach(() => {
    fixture = TestBed.createComponent(DateInputComponent);
    component = fixture.componentInstance;
    component.form = new FormGroup({});

    component.form.addControl('dob', dob);
    component.input = {
      id: 'dob',
      type: 'date',
      title: 'Date of birth',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('builds the date from year month day', () => {
    component.patchDate(4, 1, 2000);
    expect(dob.value.getFullYear()).toBe(2000);
    expect(dob.value.getDate()).toBe(4);
    expect(dob.value.getMonth()).toBe(0);
    expect(component.form.valid).toBeTrue();
  });

  it('does not build an invalid date', () => {
    component.patchDate(4, 13, 2000);
    expect(dob.value).toBeNull();
    expect(component.form.valid).toBeFalse();
  });
});
