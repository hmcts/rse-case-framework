import {Type} from "@angular/core";
import {FormGroup, ValidatorFn} from "@angular/forms";
import {DynamicFormComponent, Question} from "../../../dynamic-form/dynamic-form.component";
import {CheckAnswersComponent} from "../../check-answers/types";
import {DynamicFormAnswersComponent} from "../../../dynamic-form/dynamic-form-answers.component";

export interface StepComponent {
  validate: boolean;
  valid: () => boolean;
  form: FormGroup;
  caseId?: string;
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

  buildPage(): EventBuilder;
}

export interface Event {
  steps: Array<StepType>;
  redirectTo?: string;
  location?: string;
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

export interface CustomStepBuilder<Step extends StepComponent> {
  buildPage(): EventBuilder;
  withInitializer(initialiser: (component: Step) => void): CustomStepBuilder<Step>;
  withAnswers<Answers extends CheckAnswersComponent>(answersComponent: Type<Answers>,
                                                     initialiser?: (component: Answers) => void)
                                                     : CustomStepBuilder<Step>;
  withFormGroupName(name: string): CustomStepBuilder<Step>;
}

export class EventBuilder {
  steps = new Array<StepType>();
  desc: string;
  private redirectTo: string;
  private location: string;
  constructor(private parent: EventsBuilder) {
  }

  redirectToTab(tab: string): EventBuilder {
    this.redirectTo = tab;
    return this;
  }

  atLocation(location: string): EventBuilder {
    this.location = location;
    return this;
  }

  customPage<Step extends StepComponent>(component: Type<Step>) : CustomStepBuilder<Step> {
    const parent = this;
    const step: StepType = {type: component}
    parent.steps.push(step);
    return new class implements CustomStepBuilder<Step> {
      withInitializer(initialiser: (component: Step) => void): CustomStepBuilder<Step> {
        step.initialise = initialiser;
        return this;
      }

      withAnswers<Answers extends CheckAnswersComponent>(answersComponent: Type<Answers>, initialiser?: (component: Answers) => void) {
        step.answersType = answersComponent;
        step.answerInitialise = initialiser;
        return this;
      }

      withFormGroupName(name: string): CustomStepBuilder<Step> {
        step.formGroupName = name;
        return this;
      }

      buildPage(): EventBuilder {
        return parent;
      }
    }
  }

  buildEvent(): EventsBuilder {
    return this.parent;
  }

  get(): Event {
    return {
      steps: this.steps,
      redirectTo: this.redirectTo,
      location: this.location,
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

      buildPage(): EventBuilder {
        builder.customPage(DynamicFormComponent)
          .withInitializer((x) => {
            x.title = title;
            x.questions = questions;
          })
          .withAnswers(DynamicFormAnswersComponent,
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
