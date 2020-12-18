import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {StepComponent} from '../../stepper/form-stepper/types';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-claim-value',
  templateUrl: './claim-value.component.html',
  styleUrls: ['./claim-value.component.scss']
})
export class ClaimValueComponent implements OnInit, StepComponent {
  constructor() { }

  @Input() form: FormGroup = new FormGroup({});

  validate: boolean;

  ngOnInit(): void {
    this.form.addControl('lowerValue', new FormControl(''));
    this.form.addControl('higherValue', new FormControl(''));
  }


  valid(): boolean {
    return true;
  }

}
