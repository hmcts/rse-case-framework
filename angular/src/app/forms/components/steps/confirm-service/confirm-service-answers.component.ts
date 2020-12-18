import {Component, EventEmitter, OnInit} from '@angular/core';
import {CheckAnswersComponent} from '../../check-answers/types';
import {FormGroup} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-confirm-service-answers',
  template: `
      <h3 class="govuk-heading-m">Confirm service for claim {{ caseName }} </h3>
      <dl class="govuk-summary-list govuk-!-margin-bottom-9">
          <div *ngFor="let key of ['name', 'role']" class="govuk-summary-list__row">
              <dt class="govuk-summary-list__key">
                  {{key | titlecase }}
              </dt>
              <dd class="govuk-summary-list__value">
                  {{ form.controls[key].value}}
              </dd>
              <dd class="govuk-summary-list__actions">
                  <a [routerLink]="" queryParamsHandling="merge" id="change-{{i}}" (click)="answerChange.emit(index)"
                     class="govuk-link" href="#">
                      Change<span class="govuk-visually-hidden"> name</span>
                  </a>
              </dd>
          </div>
      </dl>
  `,
})
export class ConfirmServiceAnswersComponent implements OnInit, CheckAnswersComponent {
  constructor(
    private route: ActivatedRoute,
  ) { }

  caseName: string;
  form: FormGroup;
  index: number;
  answerChange = new EventEmitter<number>();

  ngOnInit(): void {
    const entityId = this.route.snapshot.queryParamMap.get('entity_id');
    this.caseName = entityId;

  }

}
