import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { CaseListComponent } from './case-list/case-list.component';
import { SearchCasesComponent } from './search-cases/search-cases.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CaseViewComponent } from './case-details/case-view/case-view.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {MatTabsModule} from '@angular/material/tabs';
import { CaseHistoryComponent } from './case-details/case-history/case-history.component';
import { DynamicFormComponent } from './forms/dynamic-form/dynamic-form.component';
import { DynamicFormAnswersComponent } from './forms/dynamic-form/dynamic-form-answers.component';
import { DynamicFormQuestionComponent } from './forms/dynamic-form-question/dynamic-form-question.component';
import { TextInputComponent } from './forms/components/text-input/text-input.component';
import { RadioComponent } from './forms/components/radio/radio.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { PartyDetailsComponent } from './forms/components/steps/party-details/party-details.component';
import { ChooseCourtComponent } from './forms/components/steps/choose-court/choose-court.component';
import {CdkStepperModule} from "@angular/cdk/stepper";
import {CommonModule} from "@angular/common";
import {StepDirective} from "./forms/components/stepper/step.directive";
import {StepperContainerComponent} from "./forms/components/stepper/stepper-container/stepper-container.component";
import {StepperStepComponent} from "./forms/components/stepper/stepper-step/stepper-step.component";
import {FormStepperComponent} from "./forms/components/stepper/form-stepper/form-stepper.component";
import { AnswerSectionComponent } from './forms/components/check-answers/answer-section/answer-section.component';
import {CheckAnswerDirective} from "./forms/components/check-answers/types";
import {PartyDetailsAnswersComponent} from "./forms/components/steps/party-details/party-details-answers.component";
import { DateInputComponent } from './forms/components/date-input/date-input.component';
import {PartiesTabComponent} from "./case-details/tabs/parties/parties-tab.component";
import { ChoosePartiesComponent } from './forms/components/steps/choose-parties/choose-parties.component';
import {ChoosePartiesAnswersComponent} from "./forms/components/steps/choose-parties/choose-parties-answers.component";
import { ClaimsTabComponent } from './case-details/tabs/claims-tab/claims-tab.component';
import { ClaimValueComponent } from './forms/components/steps/claim-value/claim-value.component';
import { CitizenTabComponent } from './case-details/tabs/citizen-tab/citizen-tab.component';
import { ImportCitizensComponent } from './forms/components/steps/import-citizens/import-citizens.component';
import { PurgeInactiveComponent } from './forms/components/steps/purge-inactive/purge-inactive.component';
import { ConfirmServiceComponent } from './forms/components/steps/confirm-service/confirm-service.component';
import { ConfirmServiceAnswersComponent } from './forms/components/steps/confirm-service/confirm-service-answers.component';
import {HumanisePipe} from "./services/humanise.pipe";
import {AuthGuardService} from "./services/auth-guard.service";
import {AuthService} from "./services/auth.service";
import {HeaderComponent} from "./components/header/header.component";
import {environment} from "../environments/environment";
// import {ApiModule, BASE_PATH} from "../generated/client-lib";
import {ApiModule, BASE_PATH} from '../../build/client-lib';

@NgModule({
  declarations: [
    AppComponent,
    CaseListComponent,
    SearchCasesComponent,
    CaseViewComponent,
    PartiesTabComponent,
    CaseHistoryComponent,
    DynamicFormComponent,
    DynamicFormAnswersComponent,
    DynamicFormQuestionComponent,
    TextInputComponent,
    RadioComponent,
    CreateEventComponent,
    StepDirective,
    PartyDetailsComponent,
    PartyDetailsAnswersComponent,
    ChooseCourtComponent,
    StepperContainerComponent,
    StepperStepComponent,
    FormStepperComponent,
    AnswerSectionComponent,
    CheckAnswerDirective,
    DateInputComponent,
    ChoosePartiesComponent,
    ChoosePartiesAnswersComponent,
    ClaimsTabComponent,
    ClaimValueComponent,
    CitizenTabComponent,
    ImportCitizensComponent,
    PurgeInactiveComponent,
    ConfirmServiceComponent,
    HumanisePipe,
    ConfirmServiceAnswersComponent,
    HeaderComponent,
      ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    NoopAnimationsModule,
    MatTabsModule,
    ReactiveFormsModule,
    CdkStepperModule,
    CommonModule,
    ApiModule,
  ],
  providers: [AuthGuardService, AuthService,
    { provide: BASE_PATH, useValue: 'http://localhost:4200'}],
bootstrap: [AppComponent]
})
export class AppModule { }

