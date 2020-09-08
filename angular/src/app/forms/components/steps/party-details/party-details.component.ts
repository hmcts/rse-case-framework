import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {StepComponent} from '../../../multi-step-form/multi-step-form.component';
import {Form, FormControl, FormGroup, Validators} from '@angular/forms';
import {RadioInput} from '../../radio/radio.component';
import {Question} from '../../../dynamic-form/dynamic-form.component';
import {TextInput} from '../../text-input/text-input.component';

@Component({
  selector: 'app-party-details',
  templateUrl: './party-details.component.html',
  styleUrls: ['./party-details.component.scss']
})
export class PartyDetailsComponent implements OnInit, StepComponent {

  constructor() { }

  @Input() partyType: string = 'Claimant'
  input: RadioInput = {
    title: '',
    choices: [
      [ 'Individual', 'Individual'],
      ['Company', 'Company'],
      [ 'Organisation', 'Organisation'],
      ['SoleTrader', 'Sole Trader' ]
    ],
  };

  individualQuestions: Array<Question> = [
    { id: 'title', type: 'text' , title: 'Title', validators: Validators.required},
    { id: 'firstName', type: 'text' , title: 'First name', validators: Validators.required},
    { id: 'lastName', type: 'text' , title: 'Last name', validators: Validators.required},
    { id: 'dateOfBirth', type: 'text' , title: 'Date of birth'},
  ]

  companyQuestions: Array<Question> = [
    { id: 'name', type: 'text' , title: 'Company name'},
  ]

  organisationQuestions: Array<Question> = [
    { id: 'name', type: 'text' , title: 'Organisation name'},
  ]

  soleTraderQuestions: Array<Question> = this.individualQuestions.concat([
    { id: 'tradingName', type: 'text' , title: 'Trading as'},
  ]);

  form: FormGroup = new FormGroup({});
  formControl: FormControl;

  ngOnInit(): void {
    this.input.title = 'Select type of ' + this.partyType;
    this.formControl = new FormControl('Individual')
    this.form.addControl('partyType', this.formControl);
  }

  @Output() onSubmitted: EventEmitter<any> = new EventEmitter<any>();

  onSubmit(data: any) {
    this.onSubmitted.emit(data);
  }
}
