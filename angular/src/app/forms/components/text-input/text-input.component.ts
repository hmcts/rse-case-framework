import { Component, OnInit, Input } from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Question} from '../../dynamic-form/dynamic-form.component';

export interface TextInput extends Question{
  title: string;
}

@Component({
  selector: 'app-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.scss']
})
export class TextInputComponent implements OnInit {

  @Input() input: TextInput = { id: '', title: '', type: 'text'};
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
