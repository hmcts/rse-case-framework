import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {EventsBuilder, StepType} from "../../forms/components/stepper/form-stepper/types";
import {ChooseCourtComponent} from "../../forms/components/steps/choose-court/choose-court.component";
import {PartyDetailsComponent} from "../../forms/components/steps/party-details/party-details.component";
import {DynamicFormAnswersComponent} from "../../forms/dynamic-form/dynamic-form-answers.component";
import {PartyDetailsAnswersComponent} from "../../forms/components/steps/party-details/party-details-answers.component";
import {Validators} from "@angular/forms";
import {ChoosePartiesComponent} from "../../forms/components/steps/choose-parties/choose-parties.component";
import {ChoosePartiesAnswersComponent} from "../../forms/components/steps/choose-parties/choose-parties-answers.component";
import {ClaimValueComponent} from "../../forms/components/steps/claim-value/claim-value.component";
import {ImportCitizensComponent} from "../../forms/components/steps/import-citizens/import-citizens.component";
import {PurgeInactiveComponent} from "../../forms/components/steps/purge-inactive/purge-inactive.component";
import {ConfirmServiceComponent} from "../../forms/components/steps/confirm-service/confirm-service.component";
import {ConfirmServiceAnswersComponent} from "../../forms/components/steps/confirm-service/confirm-service-answers.component";

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages: Array<StepType>;
  files = new FormData()
  events = new EventsBuilder()
    .event('AddNotes')
      .dynamicPage('Add case notes')
        .question('notes', 'text', 'Enter notes')
        .buildPage()
      .buildEvent()
    .event('CloseCase')
      .dynamicPage('Close the case')
        .question('reason', 'text', 'Reason for closure')
        .buildPage()
      .buildEvent()
    .event('SubmitAppeal')
      .dynamicPage('Submit an appeal')
        .question('reason', 'text', 'New evidence')
        .buildPage()
      .buildEvent()
    .event('CreateClaim')
      .dynamicPage('Claim references')
        .question('claimantReference', 'text', "Claimant\'s legal representative\'s reference", [Validators.required])
        .question('defendantReference', 'text', "Defendant\'s legal representative\'s reference", [Validators.required])
        .buildPage()
    .customPage(ChooseCourtComponent)
      .withAnswers(DynamicFormAnswersComponent,(x) => {
          x.title = 'Court location'
          x.questions = [{ type: 'text', id: 'applicantPreferredCourt', title: 'Applicant\'s preferred court' }]
        })
      .buildPage()
    .customPage(PartyDetailsComponent)
      .withAnswers(PartyDetailsAnswersComponent, (x) => {
          x.title = "Applicant party details"
        })
      .withFormGroupName('claimant')
      .buildPage()
    .customPage(PartyDetailsComponent)
      .withInitializer((x) => x.partyType = 'Defendant')
      .withAnswers(PartyDetailsAnswersComponent, (x) => {
          x.title = 'Defendant party details'
        })
      .withFormGroupName( 'defendant')
      .buildPage()
    .buildEvent()
    .event('AddParty')
      .redirectToTab('parties')
      .customPage(PartyDetailsComponent)
      .withInitializer((x) => x.partyType = 'Party')
      .withAnswers(PartyDetailsAnswersComponent)
      .buildPage()
    .buildEvent()
    .event('AddClaim')
      .redirectToTab('claims')
      .customPage(ChoosePartiesComponent)
        .withAnswers(ChoosePartiesAnswersComponent)
      .buildPage()
      .customPage(ClaimValueComponent)
      .withAnswers(DynamicFormAnswersComponent, (x) => {
          x.title = 'Claim value'
          x.questions = [
            { type: 'currency', id: 'lowerValue', title: 'Claim lower value' },
          { type: 'currency', id: 'higherValue', title: 'Claim higher value' }
        ]})
      .buildPage()
    .buildEvent()
    .event('ImportCitizens')
      .redirectToTab('citizens')
      .customPage(ImportCitizensComponent)
      .withAnswers(DynamicFormAnswersComponent, (x) => {
          x.title = 'Bulk import citizen details'
          x.questions = [
            { type: 'text', id: 'fileName', title: 'File name' },
            { type: 'text', id: 'fileSize', title: 'File size' },
            ]
        })
      .buildPage()
    .buildEvent()
    .event('PurgeInactiveCitizens')
      .redirectToTab('citizens')
      .customPage(PurgeInactiveComponent)
      .withAnswers(DynamicFormAnswersComponent, (x) => {
          x.title = 'Purge inactive citizen accounts'
          x.questions = [
            { type: 'text', id: 'inactive_count', title: 'Number of inactive accounts' },
          ]
        })
      .buildPage()
    .buildEvent()
    .event('ConfirmService')
      .redirectToTab('claims')
      .customPage(ConfirmServiceComponent)
      .withAnswers(ConfirmServiceAnswersComponent)
      .buildPage()
    .buildEvent()
    .toMap();

  private caseId: string;
  private eventId: string;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.eventId = this.route.snapshot.queryParamMap.get('id');
    this.caseId = this.route.snapshot.paramMap.get('id');
    this.route.paramMap.subscribe(x => this.caseId = x.get('id'))
    if (null == this.eventId) {
      this.eventId = "CreateClaim";
    }
    this.pages = this.events.get(this.eventId).steps
  }


  onSubmit(data): void {
    const isFile = this.files.has('file')
    const payload = isFile
      ? this.files
      : {
        id: this.eventId,
        data: data,
      };
    let url = '/api/cases';
    if (this.caseId) {
      url += '/' + this.caseId + (isFile
        ? '/files'
        : '/events')
    }
    this.http.post(this.baseUrl + url, payload, { observe: 'response' , withCredentials: true })
      .subscribe(resp => {
        const redirectTo = this.events.get(this.eventId).redirectTo;
        console.warn(redirectTo)
        console.warn(this.eventId)

        if (redirectTo) {
          this.router.navigateByUrl(`/cases/${this.caseId}?tab=${redirectTo}`, {replaceUrl: true})
        } else {
          this.router.navigateByUrl(resp.headers.get('location'), {replaceUrl: true})
        }
      });
  }

}
