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
        .build()
      .build()
    .event('CloseCase')
      .dynamicPage('Close the case')
        .question('reason', 'text', 'Reason for closure')
        .build()
      .build()
    .event('SubmitAppeal')
      .dynamicPage('Submit an appeal')
        .question('reason', 'text', 'New evidence')
        .build()
      .build()
    .event('CreateClaim')
      .dynamicPage('Claim references')
        .question('claimantReference', 'text', "Claimant\'s legal representative\'s reference", [Validators.required])
        .question('defendantReference', 'text', "Defendant\'s legal representative\'s reference", [Validators.required])
        .build()
      .customPage(ChooseCourtComponent, null, DynamicFormAnswersComponent, (x) => {
        x.title = 'Court location'
        x.questions = [{ type: 'text', id: 'applicantPreferredCourt', title: 'Applicant\'s preferred court' }]
      })
      .customPage(PartyDetailsComponent, null, PartyDetailsAnswersComponent, (x) => {
        x.title = "Applicant party details"
      }, 'claimant')
      .customPage(PartyDetailsComponent, (x) => x.partyType = 'Defendant', PartyDetailsAnswersComponent, (x) => {
        x.title = 'Defendant party details'
      }, 'defendant')
    .build()
    .event('AddParty')
      .redirectToTab('parties')
      .customPage(PartyDetailsComponent, (x) => x.partyType = 'Party', PartyDetailsAnswersComponent)
      .build()
    .event('AddClaim')
      .redirectToTab('claims')
      .customPage(ChoosePartiesComponent, null, ChoosePartiesAnswersComponent)
      .customPage(ClaimValueComponent, null, DynamicFormAnswersComponent, (x) => {
        x.title = 'Claim value'
        x.questions = [
          { type: 'currency', id: 'lowerValue', title: 'Claim lower value' },
        { type: 'currency', id: 'higherValue', title: 'Claim higher value' }
      ]})
    .build()
    .event('ImportCitizens')
      .redirectToTab('citizens')
      .customPage(ImportCitizensComponent, null, DynamicFormAnswersComponent, (x) => {
        x.title = 'Bulk import citizen details'
        x.questions = [
          { type: 'text', id: 'fileName', title: 'File name' },
          { type: 'text', id: 'fileSize', title: 'File size' },
          ]
      }, null)
    .build()
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
