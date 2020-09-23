import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {StepComponent} from "../../stepper/form-stepper/types";

@Component({
  selector: 'app-choose-court',
  templateUrl: './choose-court.component.html',
  styleUrls: ['./choose-court.component.scss']
})
export class ChooseCourtComponent implements OnInit, StepComponent {

  @Input() form: FormGroup = new FormGroup({});

  constructor() { }

  ngOnInit(): void {
    this.form.addControl('applicantPreferredCourt', new FormControl());
  }

  validate: boolean;

  valid(): boolean {
    return this.form.valid;
  }

}
