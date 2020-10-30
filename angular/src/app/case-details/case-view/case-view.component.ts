import { Component, ViewEncapsulation, OnInit } from '@angular/core';
import {Router, ActivatedRoute, ParamMap, Params} from '@angular/router';
import {Location} from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';


@Component({
  selector: 'app-case-view',
  templateUrl: './case-view.component.html',
  styleUrls: ['./case-view.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CaseViewComponent implements OnInit {
  baseUrl = environment.baseUrl;
  case: any;
  events: any = [];
  selectedValue: any;
  selectedIndex: number;
  tabMap = {
    history: 0,
    parties: 1,
    claims: 2,
    citizens:3,
  };

  eventDescriptions = {
    AddNotes: 'Add case notes',
    CloseCase: 'Close the case',
    AddParty: 'Add a party',
    AddClaim: 'Create a new claim',
    SubmitAppeal: 'Submit an appeal',
    ImportCitizens: 'Import citizens',
    PurgeInactiveCitizens: 'Purge inactive citizens',
  }

  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
  ) { }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id');
    if (null != id) {
      this.http.get(this.baseUrl + '/api/cases/' + id, { withCredentials: true }).subscribe(result => {
        this.case = result
        this.selectedValue = this.case.actions[0]
      });
      this.http.get(this.baseUrl + '/api/cases/' + id + '/EVENTS', { withCredentials: true }).subscribe(result => this.events = result);
    }

    const tab = this.route.snapshot.queryParamMap.get('tab');
    this.selectedIndex = this.tabMap[tab];
  }

  actions() {
    return this.case.actions.sort()
  }

  onEvent() {
    let id = this.route.snapshot.paramMap.get('id');
    this.router.navigateByUrl('/cases/' + id + '/create-event?id=' + this.selectedValue);
  }

  backClicked() {
    this.router.navigateByUrl('/cases')
  }

  // Update the address bar URL to track the active tab.
  onTabChange($event) {
    let value = 'history';
    for (const key in this.tabMap) {
      if (this.tabMap[key] == $event) {
        value = key;
        break;
      }
    }

    const queryParams: Params = { tab: value };
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: queryParams,
        queryParamsHandling: 'merge',
      });
  }
}
