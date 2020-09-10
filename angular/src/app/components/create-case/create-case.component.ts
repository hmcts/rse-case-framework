import {Component, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import {PartyDetailsComponent} from '../../forms/components/steps/party-details/party-details.component';
import {ChooseCourtComponent} from '../../forms/components/steps/choose-court/choose-court.component';
import {Validators} from "@angular/forms";
import {StepBuilder} from "../../forms/components/stepper/form-stepper/types";
import {DynamicFormAnswersComponent} from "../../forms/dynamic-form/dynamic-form-answers.component";
import {PartyDetailsAnswersComponent} from "../../forms/components/steps/party-details/party-details-answers.component";

@Component({
  selector: 'app-create-case',
  templateUrl: './create-case.component.html',
  styleUrls: ['./create-case.component.scss']
})
export class CreateCaseComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages = new StepBuilder()
    .dynamicPage('Claim references')
      .question('claimantReference', 'text', "Claimant\'s legal representative\'s reference", [Validators.required])
      .question('defendantReference', 'text', "Defendant\'s legal representative\'s reference", [Validators.required])
      .build()
    .customPage(ChooseCourtComponent, null, DynamicFormAnswersComponent, (x) => {
      x.title = 'Court location'
      x.questions = [{ id: 'applicantPreferredCourt', title: 'Applicant\'s preferred court' }]
    })
    .customPage(PartyDetailsComponent, null, PartyDetailsAnswersComponent, (x) => {
      x.title = "Applicant party details"
      x.role = 'claimant'
    })
    .customPage(PartyDetailsComponent, (x) => x.partyType = 'Defendant', PartyDetailsAnswersComponent, (x) => {
      x.title = 'Defendant party details'
      x.role = 'defendant';
    })
    .build();

  constructor(
    private http: HttpClient,
    private router: Router,
  ) { }

  ngOnInit(): void {
  }

  onSubmit(data): void {
    this.http.post(this.baseUrl + '/api/cases', data, { observe: 'response', withCredentials: true })
      .subscribe(resp => {
        this.router.navigateByUrl(resp.headers.get('location'), { replaceUrl: true })
      });
  }
}
