import {Component, Input, OnInit} from '@angular/core';
import {CaseService} from '../../../services/case-service.service';
import {ActivatedRoute, Router} from '@angular/router';

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
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseClaims(this.caseId).subscribe(x => {
      this.claims = x;
      this.route.paramMap.subscribe(x => {
        if (this.claims.length > 0) {
          const claimId = x.get('entity_id') ?? this.claims[0].claim_id;
          const tab = x.get('case_tab');
          if (tab === 'claims') {
            this.onSelect(claimId);
          }
        }
      });
    });
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

  onSelect(claimId: any): void {
    if (claimId) {
      this.selectedClaim = this.claims.find(x => x.claim_id == claimId);
      this.caseService.getClaimEvents(claimId).subscribe(x => this.history = x);
      this.router.navigateByUrl(`/cases/${this.caseId}/claims/${claimId}`);
    }
  }
}
