import {Component, EventEmitter, OnInit} from '@angular/core';
import {CheckAnswersComponent} from "../../check-answers/types";
import {FormGroup} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../../environments/environment";

@Component({
  selector: 'app-confirm-service-answers',
  template: `
    <h3 class="govuk-heading-m">Confirm service for {{ caseName }} </h3>
    <dl class="govuk-summary-list govuk-!-margin-bottom-9">
      <div *ngFor="let key of ['name', 'role']" class="govuk-summary-list__row">
        <dt class="govuk-summary-list__key">
          {{key | titlecase }}
        </dt>
        <dd class="govuk-summary-list__value">
          {{ form.controls[key].value}}
        </dd>
        <dd class="govuk-summary-list__actions">
          <a [routerLink]="" id="change-{{i}}" (click)="onChange.emit(index)" class="govuk-link" href="#">
            Change<span class="govuk-visually-hidden"> name</span>
          </a>
        </dd>
      </div>
    </dl>
  `,
  styleUrls: ['./confirm-service-answers.component.scss']
})
export class ConfirmServiceAnswersComponent implements OnInit, CheckAnswersComponent {

  baseUrl = environment.baseUrl;
  private case: any;
  private parties: any;
  caseName: string;
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id');
    let claimId = this.route.snapshot.queryParamMap.get('claimId')
    if (null != id) {
      this.http.get(this.baseUrl + '/api/cases/' + id, { withCredentials: true }).subscribe(result => {
        this.case = result
        this.parties = this.case.data.parties;
        const claim = this.case.data.claims[claimId]
        this.caseName = this.partyName(claim.claimantIds[0]) + ' vs ' + this.partyName(claim.defendantIds[0])
      });
    }
  }

  partyName(partyId: number) : string {
    const party = this.parties[partyId - 1];
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName
    }
  }

  form: FormGroup;
  index: number;
  onChange = new EventEmitter<number>()

}
