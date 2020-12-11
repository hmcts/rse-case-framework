import {Component, Input, EventEmitter, OnInit} from "@angular/core";
import {FormGroup} from "@angular/forms";
import {CheckAnswersComponent} from "../../check-answers/types";
import {ActivatedRoute} from "@angular/router";
import {CaseService} from "../../../../services/case-service.service";

@Component({
  selector: 'app-party-details-answers',
  template: `
    <ng-container *ngFor="let type of ['claimants', 'defendants']">
    <h2 class="govuk-heading-m">{{type | titlecase}}</h2>

    <dl *ngIf="this.caseParties" class="govuk-summary-list govuk-!-margin-bottom-9">
      <div *ngFor="let party of filterParties(type); let i = index" class="govuk-summary-list__row">
        <dt class="govuk-summary-list__key">
          {{ partyName(party.data) }}
        </dt>
        <dd class="govuk-summary-list__actions">
          <a [routerLink]="" queryParamsHandling="merge" id="change-{{i}}" (click)="onChange.emit(index)" class="govuk-link" href="#">
            Change<span class="govuk-visually-hidden"> name</span>
          </a>
        </dd>
      </div>
    </dl>
    </ng-container>
  `
})
export class ChoosePartiesAnswersComponent implements CheckAnswersComponent, OnInit {
  onChange = new EventEmitter<number>();
  @Input() index: number;
  @Input() form: FormGroup;
  private caseParties: any;

  constructor(
    private caseService: CaseService,
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    const caseId = this.route.snapshot.paramMap.get('case_id')
    this.caseService.getCaseParties(caseId).subscribe( c => {
      this.caseParties = c
    });
  }

  filterParties(type) {
    // @ts-ignore
    return this.caseParties.filter(x => this.form.controls[type].controls[x.party_id]?.value)
  }

  partyName(party: any) : string {
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName
    }
  }
}
