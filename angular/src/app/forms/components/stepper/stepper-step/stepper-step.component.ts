import {
  Component,
  ComponentFactoryResolver,
  Input, OnChanges,
  OnInit, SimpleChanges,
  ViewChild,
} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {StepDirective} from "../step.directive";
import {StepComponent, StepType} from "../form-stepper/types";

@Component({
  selector: 'app-stepper-step',
  templateUrl: './stepper-step.component.html',
  styleUrls: ['./stepper-step.component.scss']
})
export class StepperStepComponent implements OnInit, OnChanges {

  @ViewChild(StepDirective, {static: true}) adHost: StepDirective;
  @Input() step: StepType;
  @Input() validate: boolean = false;
  @Input() form: FormGroup;
  @Input() files: FormData;
  component: StepComponent;


  constructor(
    private componentFactoryResolver: ComponentFactoryResolver)
  { }

  ngOnInit(): void {
    if (!this.step) {
      return;
    }
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.step.type);

    const viewContainerRef = this.adHost.viewContainerRef;
    viewContainerRef.clear();

    const step = viewContainerRef.createComponent<StepComponent>(componentFactory);
    this.component = step.instance;
    if (this.step.initialise) {
      this.step.initialise(step.instance);
    }
    step.instance.form = this.form;
    step.instance.files = this.files;
  }

  valid(): boolean {
    if (!this.component) {
      return undefined;
    }
    let result = this.component.valid();
    return result;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.component) {
      this.component.validate = this.validate;
    }
  }
}
