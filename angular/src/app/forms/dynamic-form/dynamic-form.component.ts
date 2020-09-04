import {Component, OnInit, Output, EventEmitter, Input, OnChanges, SimpleChanges} from '@angular/core';
import {Location} from '@angular/common';
import { FormControl, FormGroup, FormBuilder, FormArray } from '@angular/forms';
import {StepComponent} from '../multi-step-form/multi-step-form.component';

export interface Question {
  id: string;
  type: string;
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
        let c = new FormControl('');
        this.form.addControl(question.id, c);
      }
    }
  }

  onSubmit() {
    this.onSubmitted.emit(this.form.value)
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.buildForm()
  }

  @Output() onSubmitted: EventEmitter<any> = new EventEmitter<any>();
}
