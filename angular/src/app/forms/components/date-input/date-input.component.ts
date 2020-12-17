import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {Question} from '../../dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-date-input',
  templateUrl: './date-input.component.html',
  styleUrls: ['./date-input.component.scss']
})
export class DateInputComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() input: Question;
  @Input() validate: boolean;

  group = new FormGroup({
    day: new FormControl(),
    month: new FormControl(),
    year: new FormControl(),
  });

  constructor() { }

  ngOnInit(): void {
    this.group.valueChanges.subscribe(x => {
      this.patchDate(this.group.controls.day.value, this.group.controls.month.value, this.group.controls.year.value);
    });

    this.form.controls[this.input.id].setValidators(Validators.required);
  }

  patchDate(day: number, month: number, year: number): void {
    const date = new Date(year, month - 1, day);
    if (date.getFullYear() == year && date.getMonth() == month - 1 && date.getDate() == day) {
      this.form.controls[this.input.id].patchValue(date);
    } else {
      this.form.controls[this.input.id].patchValue(null);
    }
  }

  valid(): boolean {
    if (!this.validate) {
      return true;
    }
    return this.form.controls[this.input.id].valid;
  }
}
