import {Component, OnInit} from '@angular/core';
import { DynamicFormComponent } from 'src/app/forms/dynamic-form/dynamic-form.component';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import {PartyDetailsComponent} from '../../forms/components/steps/party-details/party-details.component';
import {ChooseCourtComponent} from '../../forms/components/steps/choose-court/choose-court.component';
import {StepBuilder} from '../../forms/multi-step-form/multi-step-form.component';

@Component({
  selector: 'app-create-case',
  templateUrl: './create-case.component.html',
  styleUrls: ['./create-case.component.scss']
})
export class CreateCaseComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages = new StepBuilder()
    .page(DynamicFormComponent, (x) => {
      x.title = 'Claim references';
      x.questions = [
        { id: 'claimantReference', type: 'text', title: 'Claimant\'s legal representative\'s reference'},
        { id: 'defendantReference', type: 'text', title: 'Defendant\'s legal representative\'s reference'},
      ];
    })
    .page(ChooseCourtComponent)
    .page(PartyDetailsComponent, null, 'claimant')
    .page(PartyDetailsComponent, (x) => x.partyType = 'Defendant', 'defendant')
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
