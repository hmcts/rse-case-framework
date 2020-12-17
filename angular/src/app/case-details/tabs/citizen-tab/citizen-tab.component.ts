import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {FormControl, FormGroup} from '@angular/forms';
import {CitizenControllerService} from '../../../../generated/client-lib';

@Component({
  selector: 'app-citizen-tab',
  templateUrl: './citizen-tab.component.html',
  styleUrls: ['./citizen-tab.component.scss']
})
export class CitizenTabComponent implements OnInit {

  @Input() caseId: any;
  baseUrl = environment.baseUrl;
  citizens;
  page = 1;
  pageSize = 10;
  searchForm: FormGroup;
  hasMore: any;

  constructor(
    private http: HttpClient,
    private citizenService: CitizenControllerService,
  ) { }

  ngOnInit(): void {
    this.searchForm = new FormGroup({
      forename: new FormControl(),
      surname: new FormControl(),
    });
    this.fetch();
  }

  fetch() {
    if (!this.caseId) {
      return;
    }
    const query = btoa(JSON.stringify(this.searchForm.value));
    this.citizenService.getCitizens(this.caseId, query, this.page).subscribe(x => {
      this.hasMore = x.hasMore;
      this.citizens = x.citizens;
    });
  }

  next() {
    this.page++;
    this.fetch();
  }

  previous() {
    this.page = Math.max(1, this.page - 1);
    this.fetch();
  }

  startIndex() {
    return Math.max(1, (this.page - 1) * this.pageSize);
  }

  onSubmit() {
    this.page = 1;
    this.fetch();
  }
}
