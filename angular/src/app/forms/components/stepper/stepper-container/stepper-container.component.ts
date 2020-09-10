import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output,} from '@angular/core';
import {CdkStepper} from "@angular/cdk/stepper";
import {ActivatedRoute } from "@angular/router";
import {Directionality} from "@angular/cdk/bidi";

@Component({
  selector: 'app-stepper-container',
  templateUrl: './stepper-container.component.html',
  styleUrls: ['./stepper-container.component.scss'],
  // This custom stepper provides itself as CdkStepper so that it can be recognized
  // by other components.
  providers: [{ provide: CdkStepper, useExisting: StepperContainerComponent }]
})
export class StepperContainerComponent extends CdkStepper implements OnInit {

  constructor(
    dir: Directionality,
    changeDetectorRef: ChangeDetectorRef,
    private route: ActivatedRoute,
  ) {
    super(dir, changeDetectorRef);
  }

  @Output() nextEvent = new EventEmitter<any>();
  ngOnInit(): void {
    this.selectedIndex = Number(this.route.snapshot.queryParamMap.get('step'))
  }

  onNext() {
    this.nextEvent.emit('')
  }
}
