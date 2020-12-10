import {Component, Input, OnInit} from '@angular/core';
import {CaseService} from '../../../services/case-service.service';

@Component({
  selector: 'app-claims-tab',
  templateUrl: './claims-tab.component.html',
  styleUrls: ['./claims-tab.component.scss']
})
export class ClaimsTabComponent implements OnInit {
  claims: Array<any>;
  @Input() caseId = '1';
  selectedClaim: any;
  private history: any;

  constructor(
    private caseService: CaseService,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseClaims(this.caseId).subscribe(x => {
      this.claims = x;
      this.onSelect(this.claims[0]);
    })
  }

  partyName(party: any): string {
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName
    }
  }

  claimantName(claim: any): string {
    return this.partyName(claim.parties.claimants[0]);
  }

  claimName(claim: any): string {
    return this.partyName(claim.parties.claimants[0])
      + (claim.parties.claimants.length > 1 ? ' et al' : '')
      + ' vs '
      + this.partyName(claim.parties.defendants[0])
      + (claim.parties.defendants.length > 1 ? ' et al' : '');
  }

  onSelect(claim: any): void {
    if (claim) {
      this.selectedClaim = claim;
      this.caseService.getClaimEvents(claim.claim_id).subscribe(x => this.history = x);
    }
  }
}
