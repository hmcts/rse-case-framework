import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-case-list',
  templateUrl: './case-list.component.html',
  styleUrls: ['./case-list.component.scss']
})
export class CaseListComponent implements OnInit {

  constructor() { }
  @Input() caseList;
  ngOnInit(): void {
  }

  getPartyDisplayName(c) {
    switch (c.partyType) {
      case 'Organisation':
      case 'Company':
        {
        return c.name;
      }
      case 'Individual':
      case 'SoleTrader': {
        return c.firstName + ' ' + c.lastName
      }
    }
    return c.partyType;
  }
}
