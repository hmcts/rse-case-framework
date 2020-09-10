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
import {FormArray, FormGroup} from "@angular/forms";
import {StepType} from "./types";
import {StepperContainerComponent} from "../stepper-container/stepper-container.component";
import {StepperStepComponent} from "../stepper-step/stepper-step.component";

@Component({
  selector: 'app-form-stepper',
  templateUrl: './form-stepper.component.html',
  styleUrls: ['./form-stepper.component.scss']
})
export class FormStepperComponent implements OnInit {

  @ViewChild(StepperContainerComponent, { static: true}) stepper: StepperContainerComponent
  @ViewChildren(StepperStepComponent) children: QueryList<StepperStepComponent>
  @Input() pages: Array<StepType> = [];
  @Output() onSubmit = new EventEmitter<any>()
  validate = false;
  forms = new FormArray([]);
  answers: any;
  checkingAnswers = false;

  ngOnInit(): void {
    for (const page of this.pages) {
      this.forms.push(new FormGroup({}));
    }
  }

  onNext(event:any) {
    // If on the answer's page.
    if (this.stepper.selectedIndex > this.children.length - 1) {
      // Merge the pages into a single map
      let result = this.forms.controls.map(x => x.value)
        .reduce((a, b)=> {return { ...a, ...b}}, {})
      this.onSubmit.emit(result)
    } else {
      let page = this.children.toArray()[this.stepper.selectedIndex];
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

  onChange(index: number) {
    this.stepper.selectedIndex = index;
    this.checkingAnswers = true;
  }
}
