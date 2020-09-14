import { Component, ViewEncapsulation, OnInit } from '@angular/core';
import {Router, ActivatedRoute, ParamMap, Params} from '@angular/router';
import {Location} from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import Questions from '../../../assets/schema/schema.json';


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
  schema: any = Questions;
  selectedValue: any;
  selectedIndex: number;
  tabMap = {
    history: 0,
    parties: 1,
  };


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
      this.http.get(this.baseUrl + '/api/cases/' + id + '/events', { withCredentials: true }).subscribe(result => this.events = result);
    }

    const tab = this.route.snapshot.queryParamMap.get('tab');
    this.selectedIndex = this.tabMap[tab];
  }

  onEvent() {
    let id = this.route.snapshot.paramMap.get('id');
    this.router.navigateByUrl('/cases/' + id + '/create-event?id=' + this.selectedValue);
  }

  backClicked() {
    this.location.back();
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
