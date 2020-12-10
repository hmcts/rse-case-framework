import {Component, Input, OnInit} from '@angular/core';
import {CaseService} from '../../../services/case-service.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-parties-tab',
  templateUrl: './parties-tab.component.html',
  styleUrls: ['./parties-tab.component.scss']
})
export class PartiesTabComponent implements OnInit {

  @Input() caseId: any;
  parties: any = [];
  party: any;
  currentTab: string;
  constructor(
    private caseService: CaseService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseParties(this.caseId)
      .subscribe(x =>  {
        this.parties = x;
        this.route.paramMap.subscribe(params => {
          if (this.parties.length > 0) {
            const partyId = params.get('entity_id') ?? this.parties[0].party_id;
            this.currentTab = params.get('case_tab');
            this.onSelect(partyId);
          }
        });
      });
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

  onSelect(partyId: any): void {
    if (partyId) {
      this.party = this.parties.find(x => x.party_id == partyId);
      if (this.currentTab === 'parties') {
        this.router.navigateByUrl(`/cases/${this.caseId}/parties/${partyId}`);
      }
    }
  }
}
