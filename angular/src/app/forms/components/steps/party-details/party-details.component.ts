import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Form, FormControl, FormGroup, Validators} from '@angular/forms';
import {RadioInput} from '../../radio/radio.component';
import {Question} from '../../../dynamic-form/dynamic-form.component';
import {StepComponent} from "../../stepper/form-stepper/types";

@Component({
  selector: 'app-party-details',
  templateUrl: './party-details.component.html',
  styleUrls: ['./party-details.component.scss']
})
export class PartyDetailsComponent implements OnInit, StepComponent {

  constructor() { }

  @Input() partyType: string = 'Claimant'
  @Input() form: FormGroup = new FormGroup({});

  input: RadioInput = {
    title: '',
    choices: [
      [ 'Individual', 'Individual'],
      ['Company', 'Company'],
      [ 'Organisation', 'Organisation'],
      ['SoleTrader', 'Sole Trader' ]
    ],
  };

  partyTypeControl: FormControl;

  partyTypeQuestions:{ [key: string]: Question[]}

  buildQuestions(): void {
    this.partyTypeQuestions = {}

    this.partyTypeQuestions['Individual']  = [
      { id: 'title', type: 'text' , title: 'Title', validators: Validators.required},
      { id: 'firstName', type: 'text' , title: 'First name', validators: Validators.required},
      { id: 'lastName', type: 'text' , title: 'Last name', validators: Validators.required},
      { id: 'dateOfBirth', type: 'text' , title: 'Date of birth'},
    ];

    this.partyTypeQuestions['Company'] = [
      { id: 'name', type: 'text' , title: 'Company name', validators: Validators.required},
    ];

    this.partyTypeQuestions['Organisation'] = [
      { id: 'name', type: 'text' , title: 'Organisation name', validators: Validators.required},
    ];

    this.partyTypeQuestions['SoleTrader'] = this.partyTypeQuestions['Individual'].concat([
      { id: 'tradingName', type: 'text' , title: 'Trading as'},
    ]);
  }

  ngOnInit(): void {
    this.buildQuestions();
    let group = new FormGroup({});
    this.form.addControl(this.partyType.toLowerCase(), group)
    this.form = group;
    this.input.title = 'Select type of ' + this.partyType;
    this.partyTypeControl = new FormControl('Individual')
    this.form.addControl('partyType', this.partyTypeControl);
  }

  @Output() onSubmitted: EventEmitter<any> = new EventEmitter<any>();

  onSubmit(data: any) {
    this.onSubmitted.emit(data);
  }

  validate: boolean;

  valid(): boolean {
    let questions: Question[] = this.partyTypeQuestions[this.partyTypeControl.value];
    for (const question of questions) {
      const control = this.form.controls[question.id]
      if (!control?.valid) {
        return false;
      }
    }
    return true;
  }
}
