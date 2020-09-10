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
  selected: number;
  validate = false;
  forms = new FormArray([]);

  ngOnInit(): void {
    for (const page of this.pages) {
      this.forms.push(new FormGroup({}));
    }
  }

  onNext(event:any) {
    if (this.stepper.selectedIndex > this.children.length - 1) {
      // Merge the pages into a single map
      let result = this.forms.controls.map(x => x.value)
        .reduce((a, b)=> {return { ...a, ...b}}, {})
      this.onSubmit.emit(result)
    } else {
      let page = this.children.toArray()[this.stepper.selectedIndex];
      if (page.valid()) {
        this.validate = false;
        this.stepper.next();
      } else {
        this.validate = true;
      }
    }
  }
}
