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

  values = ['Individual', 'Company'];
  @Input() input: RadioQuestion;
  @Input() form: FormGroup;
  @Input() control: FormControl;

}
