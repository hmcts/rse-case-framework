import { Component, OnInit, Input } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
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
  constructor() { }

  ngOnInit(): void {
  }

}
