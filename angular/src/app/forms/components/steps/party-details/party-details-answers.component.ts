import {Component, Input, EventEmitter} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {CheckAnswersComponent} from "../../check-answers/types";
import {PartyDetailsComponent} from "./party-details.component";
import {Question} from "../../../dynamic-form/dynamic-form.component";

@Component({
  selector: 'app-party-details-answers',
  template: `

    <h2 class="govuk-heading-m">{{ title }}</h2>
    <dl *ngIf="form.controls[role]" class="govuk-summary-list govuk-!-margin-bottom-9">
      <div *ngFor="let question of details()" class="govuk-summary-list__row">
        <dt class="govuk-summary-list__key">
          {{question.title}}
        </dt>
        <dd *ngIf="formDetails" class="govuk-summary-list__value">
          {{formDetails.value[question.id]}}
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
export class PartyDetailsAnswersComponent implements CheckAnswersComponent {
  onChange = new EventEmitter<number>();
  @Input() index: number;
  @Input() form: FormGroup;
  formDetails: any;
  @Input() title: string;
  questions = PartyDetailsComponent.buildQuestions();
  @Input() role: string;
  partyType: string;
  details(): Question[] {
    this.formDetails = this.form.controls[this.role]
    this.partyType = this.formDetails.value['partyType']
    let initial = [{ title: 'Party type', id: 'partyType', type: ''}]
    return initial.concat(this.questions[this.partyType]);
  }
}
