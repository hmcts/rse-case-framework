import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {environment} from "../../../../environments/environment";
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-citizen-tab',
  templateUrl: './citizen-tab.component.html',
  styleUrls: ['./citizen-tab.component.scss']
})
export class CitizenTabComponent implements OnInit {

  caseId: any;
  baseUrl = environment.baseUrl;
  citizens
  page = 1;
  pageSize = 10;
  searchForm: FormGroup;
  hasMore: any;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.searchForm = new FormGroup({
      forename: new FormControl(),
      surname: new FormControl(),
    })
    this.caseId = this.route.snapshot.paramMap.get('id');
    this.fetch();
  }

  fetch() {
    if (!this.caseId) {
      return;
    }
    const query = btoa(JSON.stringify(this.searchForm.value));
    const url = this.baseUrl + '/api/cases/' + this.caseId + '/citizens';
    this.http.get(url, {
      params: {
        page: this.page.toString()
      },
      headers: new HttpHeaders({ 'search-query': query})
    }).subscribe(result => {
      this.citizens = result['citizens'];
      this.hasMore = result['hasMore'];
    });
  }

  next() {
    this.page++;
    this.fetch()
  }

  previous() {
    this.page = Math.max(1, this.page - 1)
    this.fetch()
  }

  startIndex() {
    return Math.max(1, (this.page - 1) * this.pageSize)
  }

  onSubmit() {
    this.page = 1;
    this.fetch();
  }
}
