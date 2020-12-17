import {Component, Input, EventEmitter} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {CheckAnswersComponent} from '../../check-answers/types';
import {PartyDetailsComponent} from './party-details.component';
import {Question} from '../../../dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-party-details-answers',
  template: `
    <app-dynamic-form-answers [index]="index" (onChange)="onChange.emit($event)" [form]="form" [title]="title" [questions]="details()"></app-dynamic-form-answers>
  `
})
export class PartyDetailsAnswersComponent implements CheckAnswersComponent {
  onChange = new EventEmitter<number>();
  @Input() index: number;
  @Input() form: FormGroup;
  @Input() title: string;
  questions = PartyDetailsComponent.buildQuestions();
  partyType: string;
  details(): Question[] {
    this.partyType = this.form.value.partyType;
    const initial = [{ title: 'Party type', id: 'partyType', type: ''}];
    return initial.concat(this.questions[this.partyType]);
  }
}
