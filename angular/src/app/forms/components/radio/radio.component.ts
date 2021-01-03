import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {RadioQuestion} from '../../dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-radio',
  templateUrl: './radio.component.html',
  styleUrls: ['./radio.component.scss']
})
export class RadioComponent {

constructor() { }

  @Input() title: string;
  @Input() choices: [string, string][];
  @Input() form: FormGroup;
  @Input() control: FormControl;

}
