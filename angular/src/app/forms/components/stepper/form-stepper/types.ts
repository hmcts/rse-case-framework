import {EventEmitter, Type} from "@angular/core";
import {FormGroup, ValidatorFn} from "@angular/forms";
import {DynamicFormComponent, Question} from "../../../dynamic-form/dynamic-form.component";
import {CheckAnswerDirective, CheckAnswersComponent} from "../../check-answers/types";
import {DynamicFormAnswersComponent} from "../../../dynamic-form/dynamic-form-answers.component";

export interface StepComponent {
  onSubmitted: EventEmitter<any>;
  validate: boolean;
  valid: () => boolean;
  form: FormGroup;
}

export interface StepType {
  type: Type<StepComponent>;
  initialise?: (component: StepComponent) => void;
  answersType?: Type<CheckAnswersComponent>;
  answerInitialise?: (component: CheckAnswersComponent) => void;
  formGroup?: string;
}

export interface DynamicPageBuilder {
  question(id: string, type: string, title: string, validators?: ValidatorFn[] ): DynamicPageBuilder;
  questions(question: Question | Question[]): DynamicPageBuilder;

  build(): StepBuilder;
}

export class StepBuilder {
  steps = new Array<StepType>();
  customPage<Step extends StepComponent, Answer extends CheckAnswersComponent>
  (component: Type<Step>, initialiser?: (component: Step) => void ,
   answersType?: Type<Answer>, answerInitialise?: (component: Answer) => void): StepBuilder {
    this.steps.push({ type: component, initialise: initialiser, answersType, answerInitialise });
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
          }, DynamicFormAnswersComponent,
          (x) => {
            x.title = title;
            x.questions = questions
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
