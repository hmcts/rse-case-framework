import { Component, OnInit } from '@angular/core';
import {StepComponent} from '../../stepper/form-stepper/types';
import {FormControl, FormGroup} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-confirm-service',
  templateUrl: './confirm-service.component.html',
  styleUrls: ['./confirm-service.component.scss']
})
export class ConfirmServiceComponent implements OnInit, StepComponent {

  constructor(
    private route: ActivatedRoute,
  ) { }

  form: FormGroup = new FormGroup({});

  validate: boolean;

  ngOnInit(): void {
    this.form.addControl('name', new FormControl());
    this.form.addControl('role', new FormControl());
  }


  valid(): boolean {
    return true;
  }

}
