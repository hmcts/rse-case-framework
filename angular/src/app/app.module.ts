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
import { OverviewComponent } from './case-details/overview/overview.component';
import { CaseHistoryComponent } from './case-details/case-history/case-history.component';
import { DynamicFormComponent } from './forms/dynamic-form/dynamic-form.component';
import { DynamicFormAnswersComponent } from './forms/dynamic-form/dynamic-form-answers.component';
import { DynamicFormQuestionComponent } from './forms/dynamic-form-question/dynamic-form-question.component';
import { TextInputComponent } from './forms/components/text-input/text-input.component';
import { RadioComponent } from './forms/components/radio/radio.component';
import { CreateCaseComponent } from './components/create-case/create-case.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { PartyDetailsComponent } from './forms/components/steps/party-details/party-details.component';
import { ChooseCourtComponent } from './forms/components/steps/choose-court/choose-court.component';
import { LoginComponent } from './components/login/login.component';
import {CdkStepperModule} from "@angular/cdk/stepper";
import {CommonModule} from "@angular/common";
import {StepDirective} from "./forms/components/stepper/step.directive";
import {StepperContainerComponent} from "./forms/components/stepper/stepper-container/stepper-container.component";
import {StepperStepComponent} from "./forms/components/stepper/stepper-step/stepper-step.component";
import {FormStepperComponent} from "./forms/components/stepper/form-stepper/form-stepper.component";
import { AnswerSectionComponent } from './forms/components/check-answers/answer-section/answer-section.component';
import {CheckAnswerDirective} from "./forms/components/check-answers/types";
import {PartyDetailsAnswersComponent} from "./forms/components/steps/party-details/party-details-answers.component";

@NgModule({
  declarations: [
    AppComponent,
    CaseListComponent,
    SearchCasesComponent,
    CaseViewComponent,
    OverviewComponent,
    CaseHistoryComponent,
    DynamicFormComponent,
    DynamicFormAnswersComponent,
    DynamicFormQuestionComponent,
    TextInputComponent,
    RadioComponent,
    CreateCaseComponent,
    CreateEventComponent,
    StepDirective,
    PartyDetailsComponent,
    PartyDetailsAnswersComponent,
    ChooseCourtComponent,
    LoginComponent,
    StepperContainerComponent,
    StepperStepComponent,
    FormStepperComponent,
    AnswerSectionComponent,
    CheckAnswerDirective,
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
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
