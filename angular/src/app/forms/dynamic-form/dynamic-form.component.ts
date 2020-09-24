import {Component, OnInit, Output, EventEmitter, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormControl, FormGroup, ValidatorFn} from '@angular/forms';
import {StepComponent} from "../components/stepper/form-stepper/types";

export interface Question {
  id: string;
  type: string;
  title: string;
  validators?: ValidatorFn | ValidatorFn[]
  [key: string]: any;
}

@Component({
  selector: 'app-dynamic-form',
  templateUrl: './dynamic-form.component.html',
  styleUrls: ['./dynamic-form.component.scss']
})
export class DynamicFormComponent implements OnInit, OnChanges, StepComponent {

  @Input() form: FormGroup = new FormGroup({});
  type = 'radio';
  @Input() questions: Question[];
  @Input() title: string;
  @Input() validate: boolean;

  ngOnInit(): void {
    this.buildForm()
  }

  buildForm(): void {
    if (this.questions) {
      for (const question of this.questions) {
        let c = new FormControl('', question.validators);
        this.form.addControl(question.id, c);
      }
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.buildForm()
  }


  valid(): boolean {
    return this.form.valid;
  }
}
