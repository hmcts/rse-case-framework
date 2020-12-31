import {Component, OnInit, Output, EventEmitter, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, ValidatorFn} from '@angular/forms';
import {StepComponent} from '../components/stepper/linear-stepper/types';

interface Question {
  id: string;
  title: string;
  validators?: ValidatorFn | ValidatorFn[];
}

export type QuestionType = TextQuestion | DateQuestion | RadioQuestion;

export interface TextQuestion extends Question {
  type: 'text';
}

export interface DateQuestion extends Question {
  type: 'date';
}

export interface RadioQuestion extends Question {
  type: 'radio';
  choices: [string, string][];
}

@Component({
  selector: 'app-dynamic-form',
  templateUrl: './dynamic-form.component.html',
  styleUrls: ['./dynamic-form.component.scss']
})
export class DynamicFormComponent implements OnInit, OnChanges, StepComponent {

  @Input() form: FormGroup = new FormGroup({});
  @Input() questions: QuestionType[];
  @Input() title: string;
  @Input() validate: boolean;

  ngOnInit(): void {
    this.buildForm();
  }

  buildForm(): void {
    if (this.questions) {
      for (const question of this.questions) {
        const c = new FormControl('', question.validators);
        this.form.addControl(question.id, c);
      }
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.buildForm();
  }


  valid(): boolean {
    return this.form.valid;
  }
}
