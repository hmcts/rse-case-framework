import {EventsBuilder} from '../forms/components/stepper/linear-stepper/types';
import {Validators} from '@angular/forms';
import {ChooseCourtComponent} from '../forms/components/steps/choose-court/choose-court.component';
import {DynamicFormAnswersComponent} from '../forms/dynamic-form/dynamic-form-answers.component';
import {PartyDetailsComponent} from '../forms/components/steps/party-details/party-details.component';
import {PartyDetailsAnswersComponent} from '../forms/components/steps/party-details/party-details-answers.component';
import {ChoosePartiesComponent} from '../forms/components/steps/choose-parties/choose-parties.component';
import {ChoosePartiesAnswersComponent} from '../forms/components/steps/choose-parties/choose-parties-answers.component';
import {ClaimValueComponent} from '../forms/components/steps/claim-value/claim-value.component';
import {ImportCitizensComponent} from '../forms/components/steps/import-citizens/import-citizens.component';
import {PurgeInactiveComponent} from '../forms/components/steps/purge-inactive/purge-inactive.component';

export class CaseEvents {

   public static readonly EVENTS = new EventsBuilder()
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
        .question('claimantReference', 'text', 'Claimant\'s legal representative\'s reference', [Validators.required])
        .question('defendantReference', 'text', 'Defendant\'s legal representative\'s reference', [Validators.required])
        .buildPage()
    .customPage(ChooseCourtComponent)
      .withAnswers(DynamicFormAnswersComponent, (x) => {
          x.title = 'Court location';
          x.questions = [{ type: 'text', id: 'applicantPreferredCourt', title: 'Applicant\'s preferred court' }];
        })
      .buildPage()
    .customPage(PartyDetailsComponent)
      .withAnswers(PartyDetailsAnswersComponent, (x) => {
          x.title = 'Applicant party details';
        })
      .withFormGroupName('claimant')
      .buildPage()
    .customPage(PartyDetailsComponent)
      .withInitializer((x) => x.partyType = 'Defendant')
      .withAnswers(PartyDetailsAnswersComponent, (x) => {
          x.title = 'Defendant party details';
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
          x.title = 'Claim value';
          x.questions = [
            { type: 'currency', id: 'lowerValue', title: 'Claim lower value' },
          { type: 'currency', id: 'higherValue', title: 'Claim higher value' }
        ]; })
      .buildPage()
    .buildEvent()
    .event('ImportCitizens')
      .redirectToTab('citizens')
      .customPage(ImportCitizensComponent)
      .withAnswers(DynamicFormAnswersComponent, (x) => {
          x.title = 'Bulk import citizen details';
          x.questions = [
            { type: 'text', id: 'fileName', title: 'File name' },
            { type: 'text', id: 'fileSize', title: 'File size' },
            ];
        })
      .buildPage()
    .buildEvent()
    .event('PurgeInactiveCitizens')
      .redirectToTab('citizens')
      .customPage(PurgeInactiveComponent)
      .withAnswers(DynamicFormAnswersComponent, (x) => {
          x.title = 'Purge inactive citizen accounts';
          x.questions = [
            { type: 'text', id: 'inactive_count', title: 'Number of inactive accounts' },
          ];
        })
      .buildPage()
    .buildEvent()
    .toMap();
}
