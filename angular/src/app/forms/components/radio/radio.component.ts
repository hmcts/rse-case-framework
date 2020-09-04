import { Component, OnInit, Input } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';

export interface RadioInput {
  title: string;
  choices: [string, string][];
}

@Component({
  selector: 'app-radio',
  templateUrl: './radio.component.html',
  styleUrls: ['./radio.component.scss']
})
export class RadioComponent implements OnInit {

constructor() { }

  @Input() input: RadioInput = { title: '', choices: []};
  @Input() form: FormGroup;
  @Input() control: FormControl;
  ngOnInit(): void {
  }
}
