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

export class EventsBuilder {
  result = new Map<string, StepBuilder>()
  event(id: string): StepBuilder {
    const builder = new StepBuilder(this);
    this.result.set(id, builder)
    return builder;
  }

  toMap(): Map<string, Array<StepType>> {
    const result = new Map<string, Array<StepType>>()

    for (const key of this.result.keys()) {
      result.set(key, this.result.get(key).getSteps());
    }
    return result;
  }
}

export class StepBuilder {
  steps = new Array<StepType>();
  constructor(private parent: EventsBuilder) {
  }

  customPage<Step extends StepComponent, Answer extends CheckAnswersComponent>
  (component: Type<Step>, initialiser?: (component: Step) => void ,
   answersType?: Type<Answer>, answerInitialise?: (component: Answer) => void): StepBuilder {
    this.steps.push({ type: component, initialise: initialiser, answersType, answerInitialise });
    return this;
  }

  build(): EventsBuilder {
    return this.parent;
  }

  getSteps(): Array<StepType> {
    return this.steps;
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
}
