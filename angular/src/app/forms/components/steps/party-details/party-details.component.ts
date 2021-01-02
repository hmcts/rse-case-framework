import {Component, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Question, RadioQuestion} from '../../../dynamic-form/dynamic-form.component';
import {StepComponent} from '../../stepper/linear-stepper/types';
import {PartyType} from '../../../../../typings';
import {QuestionBuilder} from '../../../../events/question-builder';
import {Company, Individual, Organisation} from '../../../../../generated/client-lib';

@Component({
  selector: 'app-party-details',
  templateUrl: './party-details.component.html',
  styleUrls: ['./party-details.component.scss']
})
export class PartyDetailsComponent implements OnInit, StepComponent {

  constructor() { }

  @Input() partyType = 'Claimant';
  @Input() form: FormGroup = new FormGroup({});

  input: RadioQuestion = {
    title: '',
    id: '',
    type: 'radio',
    choices: [
      [ 'Individual', 'Individual'],
      ['Company', 'Company'],
      [ 'Organisation', 'Organisation'],
      ['SoleTrader', 'Sole Trader' ]
    ],
  };

  partyTypeControl: FormControl;

  partyTypeQuestions: { [k in PartyType]: Question[]};

  validate: boolean;

  static buildQuestions(): { [k in PartyType]: Question[]} {

    const individualQuestions = new QuestionBuilder<Individual>()
      .textField('title', 'Title')
      .textField('firstName', 'First name')
      .textField('lastName', 'Last name')
      .optionalDatefield('dateOfBirth', 'Date of birth')
      .build();

    return {
      Individual: individualQuestions,
      Company: new QuestionBuilder<Company>()
        .textField('name', 'Company name')
        .build()
      ,
      Organisation: new QuestionBuilder<Organisation>()
        .textField('name', 'Organisation name')
        .build()
      ,
      SoleTrader: individualQuestions.concat([
        {id: 'tradingName', type: 'text', title: 'Trading as'},
      ]),
    };
  }

  ngOnInit(): void {
    this.partyTypeQuestions = PartyDetailsComponent.buildQuestions();
    this.input.title = 'Select type of ' + this.partyType;
    this.partyTypeControl = new FormControl('Individual');
    this.form.addControl('partyType', this.partyTypeControl);
  }

  valid(): boolean {
    const partyType: PartyType = this.partyTypeControl.value;
    const questions: Question[] = this.partyTypeQuestions[partyType];
    for (const question of questions) {
      const control = this.form.controls[question.id];
      if (!control?.valid) {
        return false;
      }
    }
    return true;
  }
}
