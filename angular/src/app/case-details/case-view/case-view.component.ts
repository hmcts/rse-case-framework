import { Component, ViewEncapsulation, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
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
  }

  onEvent() {
    let id = this.route.snapshot.paramMap.get('id');
    this.router.navigateByUrl('/cases/' + id + '/create-event?id=' + this.selectedValue);
  }

  backClicked() {
    this.location.back();
  }
}
