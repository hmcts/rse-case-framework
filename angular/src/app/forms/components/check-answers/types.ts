import {FormGroup} from '@angular/forms';
import {Directive, EventEmitter, ViewContainerRef} from '@angular/core';

export interface CheckAnswersComponent {
  form: FormGroup;
  index: number;
  answerChange: EventEmitter<number>;
  caseId?: number;
}

@Directive({
  selector: '[appCheckAnswerHost]',
})
export class CheckAnswerDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
