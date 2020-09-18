import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-parties-tab',
  templateUrl: './parties-tab.component.html',
  styleUrls: ['./parties-tab.component.scss']
})
export class PartiesTabComponent implements OnInit {

  @Input() caseId: any;
  @Input() parties: any = [];
  constructor(
    private router: Router,
  ) { }

  ngOnInit(): void {
  }

  addParty() {
    this.router.navigateByUrl('/cases/' + this.caseId + '/create-event?id=AddParty');
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
