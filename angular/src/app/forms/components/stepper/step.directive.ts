import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appStepHost]',
})
export class StepDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
