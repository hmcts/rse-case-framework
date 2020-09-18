import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {StepComponent} from "../../stepper/form-stepper/types";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-claim-value',
  templateUrl: './claim-value.component.html',
  styleUrls: ['./claim-value.component.scss']
})
export class ClaimValueComponent implements OnInit, StepComponent {

  @Input() form: FormGroup = new FormGroup({})
  onSubmitted: EventEmitter<any>;
  constructor() { }

  ngOnInit(): void {
    this.form.addControl('lowerValue', new FormControl(''));
    this.form.addControl('higherValue', new FormControl(''));
  }


  valid(): boolean {
    return true;
  }

  validate: boolean;

}
