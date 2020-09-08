import {Component, OnInit, Output, EventEmitter, Input, OnChanges, SimpleChanges} from '@angular/core';
import {Location} from '@angular/common';
import {FormControl, FormGroup, FormBuilder, FormArray, Validator, ValidatorFn} from '@angular/forms';
import {StepComponent} from '../multi-step-form/multi-step-form.component';

export interface Question {
  id: string;
  type: string;
  validators?: ValidatorFn | ValidatorFn[]
  [key: string]: any;
}

@Component({
  selector: 'dynamic-form',
  templateUrl: './dynamic-form.component.html',
  styleUrls: ['./dynamic-form.component.scss']
})
export class DynamicFormComponent implements OnInit, OnChanges, StepComponent {

  @Input() form: FormGroup = new FormGroup({});
  type = 'radio';
  @Input() questions: Question[];
  @Input() title: string;

  constructor(
    private location: Location,
  ) { }

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

  onSubmit() {
    // Show any validation errors
    this.validate = true;
    if (this.form.valid) {
      this.onSubmitted.emit(this.form.value)
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.buildForm()
  }

  @Output() onSubmitted: EventEmitter<any> = new EventEmitter<any>();
  validate: boolean;
}
