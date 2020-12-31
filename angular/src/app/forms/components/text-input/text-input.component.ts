import { Component, OnInit, Input } from '@angular/core';
import {FormGroup} from '@angular/forms';
import {TextQuestion} from '../../dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.scss']
})
export class TextInputComponent implements OnInit {

  @Input() input: TextQuestion;
  @Input() form: FormGroup;
  @Input() validate: boolean;

  constructor() { }

  ngOnInit(): void {
  }

  valid(): boolean {
    if (!this.validate) {
      return true;
    }
    return this.form.controls[this.input.id].valid;
  }
}
