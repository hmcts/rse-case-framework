import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  EventEmitter, Input,
  OnInit,
  Output, Type,
  ViewChild
} from '@angular/core';
import {FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import Questions from '../../../assets/schema/schema.json';
import {StepDirective} from '../step.directive';
import {DynamicFormComponent, Question} from '../dynamic-form/dynamic-form.component';
import {ActivatedRoute} from '@angular/router';

export interface StepComponent {
  onSubmitted: EventEmitter<any>;
  form: FormGroup;
}

export interface StepType {
  type: Type<StepComponent>;
  initialise?: (component: StepComponent) => void;
  formGroup?: string;
}

export interface DynamicPageBuilder {
  question(id: string, type: string, title: string, validators?: ValidatorFn[] ): DynamicPageBuilder;

  build(): StepBuilder;
}

export class StepBuilder {
  steps = new Array<StepType>();
  customPage<T extends StepComponent>(component: Type<T>, initialiser?: (component: T) => void, formGroup?: string): StepBuilder {
    this.steps.push({ type: component, initialise: initialiser, formGroup });
    return this;
  }

  dynamicPage(title: string): DynamicPageBuilder {
    const builder: StepBuilder = this;
    const questions = Array<Question>();
    const result = new class implements DynamicPageBuilder {
      question(id: string, type: string, title: string, validators: ValidatorFn[] = Array()): DynamicPageBuilder {
        questions.push({ id, type, title, validators});
        return result;
      }
      build(): StepBuilder {
        builder.customPage(DynamicFormComponent, (x) => {
          x.title = title;
          x.questions = questions;
        });
        return builder;
      }
    }();
    return result;
  }

  build(): Array<StepType> {
    return this.steps;
  }
}

@Component({
  selector: 'app-multi-step-form',
  templateUrl: './multi-step-form.component.html',
  styleUrls: ['./multi-step-form.component.scss']
})
export class MultiStepFormComponent implements AfterViewInit {

  @Input() pages: Array<StepType> = []
  index = -1;
  checkAnswers: boolean = false;
  form = new FormGroup({});
  @ViewChild(StepDirective, {static: false}) stepHost: StepDirective;

  constructor(
    private formBuilder: FormBuilder,
    private componentFactory: ComponentFactoryResolver,
    private detector: ChangeDetectorRef,
    private route: ActivatedRoute,
  ) {
    this.index = Number(this.route.snapshot.queryParamMap.get('step'));
  }
  nextStep() {
    if (this.index < this.pages.length - 1) {
      this.index++;
      this.loadPage();
    } else {
      this.checkAnswers = true;
    }
  }

  loadPage() {
    if (this.pages.length === 0) {
      return;
    }
    const page = this.pages[this.index];
    const factory = this.componentFactory.resolveComponentFactory(page.type)
    const container = this.stepHost.viewContainerRef;
    container.clear();
    const component = container.createComponent<StepComponent>(factory);
    let g = this.form;
    if (page.formGroup) {
      g = new FormGroup({});
      this.form.addControl(page.formGroup, g);
    }
    component.instance.form = g;
    if (page.initialise) {
      page.initialise(component.instance);
    }
    component.instance.onSubmitted.subscribe(x => {
      this.nextStep();
    });
    this.detector.detectChanges();
  }

  ngAfterViewInit(): void {
    this.loadPage();
  }

  @Output() onSubmitted = new EventEmitter();

  submitEvent() {
    this.onSubmitted.emit(this.form.value);
  }
}
