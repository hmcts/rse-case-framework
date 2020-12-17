import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SearchCasesComponent } from './search-cases/search-cases.component';
import { CaseViewComponent } from './case-details/case-view/case-view.component';
import {CreateEventComponent} from './components/create-event/create-event.component';
import {AuthGuardService} from './services/auth-guard.service';

const routes: Routes = [
  {
    path: '', canActivate: [AuthGuardService], children: [
      { path: 'cases', component: SearchCasesComponent },
      { path: 'create-case', component: CreateEventComponent },
      { path: 'cases/:case_id/create-event', component: CreateEventComponent },
      { path: 'cases/:case_id/:case_tab/:entity_id', component: CaseViewComponent },
      { path: 'cases/:case_id/:case_tab', component: CaseViewComponent },
      { path: 'cases/:case_id', redirectTo: '/cases/:case_id/history' },
      { path: '', redirectTo: '/cases', pathMatch: 'full' },
    ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
