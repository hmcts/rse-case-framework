import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {FormArray, FormGroup} from '@angular/forms';
import {StepType} from './types';
import {StepperContainerComponent} from '../stepper-container/stepper-container.component';
import {StepperStepComponent} from '../stepper-step/stepper-step.component';

@Component({
  selector: 'app-form-stepper',
  templateUrl: './form-stepper.component.html',
  styleUrls: ['./form-stepper.component.scss']
})
export class FormStepperComponent implements OnInit {

  @ViewChild(StepperContainerComponent, { static: true}) stepper: StepperContainerComponent;
  @ViewChildren(StepperStepComponent) children: QueryList<StepperStepComponent>;
  @Input() pages: Array<StepType> = [];
  @Input() files: FormData;
  @Input() caseId: string;
  @Output() submitEvent = new EventEmitter<void>();
  validate = false;
  forms = new FormArray([]);
  checkingAnswers = false;

  ngOnInit(): void {
    for (const page of this.pages) {
      let group = new FormGroup({});
      this.forms.push(group);
      if (page.formGroupName) {
        const sub = new FormGroup({});
        group.addControl(page.formGroupName, sub);
        group = sub;
      }
      page.form = group;
    }
  }

  onNext(): void {
    // If on the answer's page.
    if (this.stepper.selectedIndex > this.children.length - 1) {
      // Merge the pages into a single map
      const result = this.forms.controls.map(x => x.value)
        .reduce((a, b) => ({ ...a, ...b}), {});
      this.submitEvent.emit(result);
    } else {
      const page = this.children.toArray()[this.stepper.selectedIndex];
      if (page.valid()) {
        this.validate = false;
        if (this.checkingAnswers) {
          // Must skip to answer confirmation
          this.stepper.selectedIndex = this.stepper.steps.length - 1;
          return;
        }
        this.stepper.next();
      } else {
        this.validate = true;
      }
    }
  }

  onChange(index: number): void {
    this.stepper.selectedIndex = index;
    this.checkingAnswers = true;
  }

  onPrevious(): void {
    this.stepper.previous();
  }
}
