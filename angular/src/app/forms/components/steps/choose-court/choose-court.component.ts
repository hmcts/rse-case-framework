import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {StepComponent} from '../../../multi-step-form/multi-step-form.component';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-choose-court',
  templateUrl: './choose-court.component.html',
  styleUrls: ['./choose-court.component.scss']
})
export class ChooseCourtComponent implements OnInit, StepComponent {

  @Input() form: FormGroup = new FormGroup({});
  onSubmitted: EventEmitter<any> = new EventEmitter<any>();

  constructor() { }

  ngOnInit(): void {
    this.form.addControl('applicantPreferredCourt', new FormControl());
  }

  onClick() {
    this.onSubmitted.emit();
  }

}
