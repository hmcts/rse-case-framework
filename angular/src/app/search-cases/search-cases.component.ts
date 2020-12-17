import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import {CaseService} from '../services/case-service.service';
import {CaseSearchResult} from '../../generated/client-lib';

@Component({
  selector: 'search-cases',
  templateUrl: './search-cases.component.html',
  styleUrls: ['./search-cases.component.scss']
})
export class SearchCasesComponent implements OnInit {

  constructor(private formBuilder: FormBuilder,
              private caseService: CaseService,
              private router: Router) { }
  searchForm;
  caseList: Array<CaseSearchResult>;
  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      caseLocalAuthority: '',
      caseName: '',
      id: '',
    });
    this.onSubmit({});
  }

  onSubmit(data) {
    this.caseService.searchCases(data).subscribe(x => this.caseList = x);
  }

  createClicked() {
    this.router.navigate(['/create-case']);
  }
}
