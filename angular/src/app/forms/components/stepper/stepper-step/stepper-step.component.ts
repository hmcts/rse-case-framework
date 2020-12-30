import {
  Component,
  ComponentFactoryResolver,
  Input, OnChanges,
  OnInit, SimpleChanges,
  ViewChild,
} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {StepDirective} from '../step.directive';
import {StepComponent, StepType} from '../linear-stepper/types';

@Component({
  selector: 'app-stepper-step',
  templateUrl: './stepper-step.component.html',
  styleUrls: ['./stepper-step.component.scss']
})
export class StepperStepComponent implements OnInit, OnChanges {

  @ViewChild(StepDirective, {static: true}) adHost: StepDirective;
  @Input() step: StepType;
  @Input() validate = false;
  @Input() form: FormGroup;
  @Input() files: FormData;
  @Input() caseId: string;
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
    step.instance.caseId = this.caseId;
  }

  valid(): boolean {
    return this.component.valid();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.component) {
      this.component.validate = this.validate;
    }
  }
}
