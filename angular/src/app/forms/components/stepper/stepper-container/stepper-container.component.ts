import { Component, EventEmitter, OnInit, Output, } from '@angular/core';
import {CdkStepper} from "@angular/cdk/stepper";

@Component({
  selector: 'app-stepper-container',
  templateUrl: './stepper-container.component.html',
  styleUrls: ['./stepper-container.component.scss'],
  // This custom stepper provides itself as CdkStepper so that it can be recognized
  // by other components.
  providers: [{ provide: CdkStepper, useExisting: StepperContainerComponent }]
})
export class StepperContainerComponent extends CdkStepper implements OnInit {

  @Output() nextEvent = new EventEmitter<any>();

  ngOnInit(): void {
  }

  onNext() {
    this.nextEvent.emit('')
  }
}
