import {Component, Input, OnInit} from '@angular/core';
import {CaseService} from '../../../services/case-service.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CaseParty} from '../../../../generated/client-lib';

@Component({
  selector: 'app-parties-tab',
  templateUrl: './parties-tab.component.html',
  styleUrls: ['./parties-tab.component.scss']
})
export class PartiesTabComponent implements OnInit {

  @Input() caseId = '1';
  parties: Array<CaseParty>;
  party: any;
  currentTab: string;
  private claims: any;
  constructor(
    private caseService: CaseService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseParties(Number(this.caseId))
      .subscribe(x =>  {
        this.parties = x;
        this.route.paramMap.subscribe(params => {
          if (this.parties.length > 0) {
            const partyId = Number(params.get('entity_id') ?? this.parties[0].partyId);
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

  onSelect(partyId: number): void {
    if (partyId) {
      const p = this.parties.find(x => x.partyId === partyId);
      if (p) {
        this.party = p.data;
        this.claims = p.claims;
        if (this.currentTab === 'parties') {
          this.router.navigateByUrl(`/cases/${this.caseId}/parties/${partyId}`);
        }
      }
    }
  }
}
