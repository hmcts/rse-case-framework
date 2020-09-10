import {Component, Input, EventEmitter} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {CheckAnswersComponent} from "../components/check-answers/types";

export interface DynamicFormCheckAnswer {
  title: string;
  id: string;
}

@Component({
  selector: 'app-dynamic-form-answers',
  template: `

    <h2 class="govuk-heading-m">{{ title }}</h2>

    <dl class="govuk-summary-list govuk-!-margin-bottom-9">
      <div *ngFor='let question of questions' class="govuk-summary-list__row">
        <dt class="govuk-summary-list__key">
          {{question.title}}
        </dt>
        <dd class="govuk-summary-list__value">
          {{form.controls[question.id].value}}
        </dd>
        <dd class="govuk-summary-list__actions">
          <a [routerLink]="" (click)="onChange.emit(index)" class="govuk-link" href="#">
            Change<span class="govuk-visually-hidden"> name</span>
          </a>
        </dd>
      </div>
    </dl>
  `
})
export class DynamicFormAnswersComponent implements CheckAnswersComponent {
  onChange = new EventEmitter<number>();
  @Input() index: number;
  @Input() form: FormGroup;
  @Input() questions: DynamicFormCheckAnswer[]
  @Input() title: string;
}
