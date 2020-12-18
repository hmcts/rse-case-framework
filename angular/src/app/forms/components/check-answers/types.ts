import {FormGroup} from '@angular/forms';
import {Directive, EventEmitter, ViewContainerRef} from '@angular/core';

export interface CheckAnswersComponent {
  form: FormGroup;
  index: number;
  answerChange: EventEmitter<number>;
  caseId?: any;
}

@Directive({
  selector: '[answerHost]',
})
export class CheckAnswerDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
