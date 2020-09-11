import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SearchCasesComponent } from './search-cases/search-cases.component';
import { CaseViewComponent } from './case-details/case-view/case-view.component';
import { DynamicFormComponent } from './forms/dynamic-form/dynamic-form.component';
import { CreateCaseComponent } from './components/create-case/create-case.component';
import {CreateEventComponent} from './components/create-event/create-event.component';

const routes: Routes = [
  { path: 'cases', component: SearchCasesComponent },
    { path: 'create-case', component: CreateCaseComponent },
  { path: 'cases/:id', component: CaseViewComponent },
  { path: 'cases/:id/create-event', component: CreateEventComponent },
  { path: '', redirectTo: '/cases', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
