import {EventEmitter, Type} from "@angular/core";
import {FormGroup, ValidatorFn} from "@angular/forms";
import {DynamicFormComponent, Question} from "../../../dynamic-form/dynamic-form.component";
import {CheckAnswerDirective, CheckAnswersComponent} from "../../check-answers/types";
import {DynamicFormAnswersComponent} from "../../../dynamic-form/dynamic-form-answers.component";

export interface StepComponent {
  validate: boolean;
  valid: () => boolean;
  form: FormGroup;
  files?: FormData;
}

export interface StepType {
  type: Type<StepComponent>;
  initialise?: (component: StepComponent) => void;
  answersType?: Type<CheckAnswersComponent>;
  answerInitialise?: (component: CheckAnswersComponent) => void;
  formGroupName?: string;
}

export interface DynamicPageBuilder {
  question(id: string, type: string, title: string, validators?: ValidatorFn[] ): DynamicPageBuilder;
  questions(question: Question | Question[]): DynamicPageBuilder;

  build(): EventBuilder;
}

export interface Event {
  steps: Array<StepType>;
  redirectTo?: string;
}

export class EventsBuilder {
  result = new Map<string, EventBuilder>()
  event(id: string): EventBuilder {
    const builder = new EventBuilder(this);
    this.result.set(id, builder)
    return builder;
  }

  toMap(): Map<string, Event> {
    const result = new Map<string, Event>()

    for (const key of this.result.keys()) {
      result.set(key, this.result.get(key).get());
    }
    return result;
  }
}

export class EventBuilder {
  steps = new Array<StepType>();
  desc: string;
  private redirectTo: string;
  constructor(private parent: EventsBuilder) {
  }

  redirectToTab(tab: string): EventBuilder {
    this.redirectTo = tab;
    return this;
  }

  customPage<Step extends StepComponent, Answer extends CheckAnswersComponent>
  (component: Type<Step>, initialiser?: (component: Step) => void ,
   answersType?: Type<Answer>, answerInitialise?: (component: Answer) => void,
   formGroupName?: string
   ): EventBuilder {
    this.steps.push({ type: component, initialise: initialiser, answersType, answerInitialise, formGroupName: formGroupName});
    return this;
  }

  build(): EventsBuilder {
    return this.parent;
  }

  get(): Event {
    return {
      steps: this.steps,
      redirectTo: this.redirectTo,
    };
  }

  dynamicPage(title: string): DynamicPageBuilder {
    const builder: EventBuilder = this;
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

      build(): EventBuilder {
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
