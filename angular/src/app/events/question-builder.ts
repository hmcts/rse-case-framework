import {KeysOfType} from '../../typings';
import {Question} from '../forms/dynamic-form/dynamic-form.component';
import {Validators} from '@angular/forms';

export class QuestionBuilder<T> {
  private questions = new Array<Question>();
  build(): Question[] {
    return this.questions;
  }

  textField(property: KeysOfType<T, string>, title: string): QuestionBuilder<T> {
    this.questions.push(
      {id: property as string, type: 'text', title, validators: Validators.required}
    );
    return this;
  }

  optionalDatefield(property: KeysOfType<T, string>, title: string): QuestionBuilder<T> {
    this.questions.push(
      {id: property as string, type: 'date', title}
    );
    return this;
  }
}
