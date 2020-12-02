import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {CaseService} from "../../../services/case-service.service";

@Component({
  selector: 'app-parties-tab',
  templateUrl: './parties-tab.component.html',
  styleUrls: ['./parties-tab.component.scss']
})
export class PartiesTabComponent implements OnInit {

  @Input() caseId: any;
  @Input() parties: any = [];
  constructor(
    private caseService: CaseService,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseParties(this.caseId)
      .subscribe(x => this.parties = x);
  }

  partyName(party: any) : string {
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName
    }
  }
}
