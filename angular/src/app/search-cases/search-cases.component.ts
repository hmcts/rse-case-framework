import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import { Router } from '@angular/router';
import {CaseService} from '../services/case-service.service';
import {CaseSearchResult} from '../../generated/client-lib';

@Component({
  selector: 'app-search-cases',
  templateUrl: './search-cases.component.html',
  styleUrls: ['./search-cases.component.scss']
})
export class SearchCasesComponent implements OnInit {

  constructor(private formBuilder: FormBuilder,
              private caseService: CaseService,
              private router: Router) { }
  searchForm: FormGroup;
  caseList: Array<CaseSearchResult>;
  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      caseLocalAuthority: '',
      caseName: '',
      id: '',
    });
    this.onSubmit();
  }

  onSubmit(): void {
    this.caseService.searchCases(JSON.stringify(this.searchForm.value)).subscribe(x => this.caseList = x);
  }

  createClicked(): void {
    this.router.navigate(['/create-case']);
  }
}
