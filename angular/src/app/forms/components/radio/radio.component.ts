import {Component, OnInit, Input, OnChanges, SimpleChanges} from '@angular/core';
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
export class RadioComponent {

constructor() { }

  values = ['Individual', 'Company']
  @Input() input: RadioInput = { title: '', choices: []};
  @Input() form: FormGroup;
  @Input() control: FormControl;

}
