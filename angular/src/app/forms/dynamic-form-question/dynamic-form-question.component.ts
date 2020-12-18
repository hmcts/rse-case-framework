import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {Question} from '../dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-dynamic-form-question',
  templateUrl: './dynamic-form-question.component.html',
  styleUrls: ['./dynamic-form-question.component.scss']
})
export class DynamicFormQuestionComponent {

  @Input() question: Question;
  @Input() form: FormGroup;
  @Input() validate: boolean;

  constructor() { }

}
