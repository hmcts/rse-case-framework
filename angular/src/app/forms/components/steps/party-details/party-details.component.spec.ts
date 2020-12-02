import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PartyDetailsComponent } from './party-details.component';
import {DynamicFormComponent} from "../../../dynamic-form/dynamic-form.component";
import {RadioComponent} from "../../radio/radio.component";
import {DynamicFormQuestionComponent} from "../../../dynamic-form-question/dynamic-form-question.component";
import {TextInputComponent} from "../../text-input/text-input.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

describe('PartyDetailsComponent', () => {
  let component: PartyDetailsComponent;
  let fixture: ComponentFixture<PartyDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule],
      declarations: [ PartyDetailsComponent,
        DynamicFormComponent,
        RadioComponent,
        DynamicFormQuestionComponent,
        TextInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PartyDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges()
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('is invalid for Organisations with no org name entered', () => {
    component.partyTypeControl.setValue('Organisation')
    expect(component.valid()).toBeFalse()
  });

  it('is valid for Organisations with an org name entered', () => {
    component.partyTypeControl.setValue('Organisation')
    fixture.detectChanges()
    component.form.patchValue( { name: 'Acme'})
    expect(component.valid()).toBeTrue()
  });

  it('is invalid for Individuals with no details', () => {
    component.partyTypeControl.setValue('Individual')
    expect(component.valid()).toBeFalse()
  });

  it('is valid for Individuals with all details', () => {
    component.partyTypeControl.setValue('Individual')
    fixture.detectChanges()
    component.form.patchValue( {
      title: 'Dr',
      firstName: 'A',
      lastName: 'Robotnik',
    })
    expect(component.valid()).toBeTrue()
  });
});
