import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router'
import { CaseService } from '../case-service.service'

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
  caseList;
  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      caseLocalAuthority: '',
      caseName: '',
      id: '',
    });
    this.caseList = this.caseService.searchCases({});
  }

  onSubmit(data) {
    this.caseList = this.caseService.searchCases(data)
  }

  createClicked() {
    this.router.navigate(['/create-case']);
  }
}
