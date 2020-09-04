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
import { DynamicFormQuestionComponent } from './forms/dynamic-form-question/dynamic-form-question.component';
import { TextInputComponent } from './forms/components/text-input/text-input.component';
import { RadioComponent } from './forms/components/radio/radio.component';
import { CreateCaseComponent } from './components/create-case/create-case.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { MultiStepFormComponent } from './forms/multi-step-form/multi-step-form.component';
import {StepDirective} from './forms/step.directive';
import { PartyDetailsComponent } from './forms/components/steps/party-details/party-details.component';
import { ChooseCourtComponent } from './forms/components/steps/choose-court/choose-court.component';
import { LoginComponent } from './components/login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    CaseListComponent,
    SearchCasesComponent,
    CaseViewComponent,
    OverviewComponent,
    CaseHistoryComponent,
    DynamicFormComponent,
    DynamicFormQuestionComponent,
    TextInputComponent,
    RadioComponent,
    CreateCaseComponent,
    CreateEventComponent,
    MultiStepFormComponent,
    StepDirective,
    PartyDetailsComponent,
    ChooseCourtComponent,
    LoginComponent,
      ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    NoopAnimationsModule,
    MatTabsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
