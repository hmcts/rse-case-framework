import {Component, ComponentFactoryResolver, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {StepComponent, StepType} from '../../stepper/linear-stepper/types';
import {StepDirective} from '../../stepper/step.directive';
import {FormGroup} from '@angular/forms';
import {CheckAnswerDirective, CheckAnswersComponent} from '../types';

@Component({
  selector: 'app-answer-section',
  templateUrl: './answer-section.component.html',
  styleUrls: ['./answer-section.component.scss']
})
export class AnswerSectionComponent implements OnInit, CheckAnswersComponent {

  @ViewChild(CheckAnswerDirective, {static: true}) answerHost: CheckAnswerDirective;
  @Input() step: StepType;
  @Input() form: FormGroup;
  @Input() index: number;
  @Input() caseId: string;
  @Output() answerChange = new EventEmitter<number>();
  component: CheckAnswersComponent;

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver)
  { }

  ngOnInit(): void {
    if (!this.step?.answersType) {
      return;
    }
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.step.answersType);

    const viewContainerRef = this.answerHost.viewContainerRef;
    viewContainerRef.clear();

    const step = viewContainerRef.createComponent<CheckAnswersComponent>(componentFactory);
    this.component = step.instance;
    if (this.step.answerInitialise) {
      this.step.answerInitialise(step.instance);
    }
    this.component.caseId = this.caseId;
    this.component.index = this.index;
    this.component.answerChange.subscribe((x: number) => this.answerChange.emit(x));
    this.component.form = this.form;
  }

}
