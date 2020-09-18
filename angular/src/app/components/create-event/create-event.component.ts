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

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages: Array<StepType>;
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
      .customPage(PartyDetailsComponent, (x) => x.partyType = 'Party', PartyDetailsAnswersComponent)
      .build()
    .event('AddClaim')
      .customPage(ChoosePartiesComponent, null, ChoosePartiesAnswersComponent)
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
    if (null == this.eventId) {
      this.eventId = "CreateClaim";
    }
    this.pages = this.events.get(this.eventId).steps
  }


  onSubmit(data): void {
    const payload = {
      id: this.eventId,
      data: data,
    };
    let url = '/api/cases';
    if (this.caseId) {
      url += '/' + this.caseId + '/events'
    }
    this.http.post(this.baseUrl + url, payload, { observe: 'response' , withCredentials: true })
      .subscribe(resp => {
        this.router.navigateByUrl(resp.headers.get('location'), { replaceUrl: true })
      });
  }

}
