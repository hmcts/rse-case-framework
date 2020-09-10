import {EventEmitter, Type} from "@angular/core";
import {FormGroup, ValidatorFn} from "@angular/forms";
import {DynamicFormComponent, Question} from "../../../dynamic-form/dynamic-form.component";

export interface StepComponent {
  onSubmitted: EventEmitter<any>;
  validate: boolean;
  valid: () => boolean;
  form: FormGroup;
}

export interface StepType {
  type: Type<StepComponent>;
  initialise?: (component: StepComponent) => void;
  formGroup?: string;
}

export interface DynamicPageBuilder {
  question(id: string, type: string, title: string, validators?: ValidatorFn[] ): DynamicPageBuilder;
  questions(question: Question | Question[]): DynamicPageBuilder;

  build(): StepBuilder;
}

export class StepBuilder {
  steps = new Array<StepType>();
  customPage<T extends StepComponent>(component: Type<T>, initialiser?: (component: T) => void): StepBuilder {
    this.steps.push({ type: component, initialise: initialiser});
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
      questions(question: Question | Question[] ): DynamicPageBuilder {
        if (question instanceof Array) {
          for (const q of question) {
            questions.push(q)
          }
        } else{
          questions.push(question);
        }
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
