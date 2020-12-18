import {Component, Input, OnInit} from '@angular/core';
import {CaseService} from '../../../services/case-service.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiEventHistory, Claim, Party} from '../../../../generated/client-lib';

@Component({
  selector: 'app-claims-tab',
  templateUrl: './claims-tab.component.html',
  styleUrls: ['./claims-tab.component.scss']
})
export class ClaimsTabComponent implements OnInit {
  claims: Array<Claim>;
  @Input() caseId = 1;
  selectedClaim: Claim;
  private history: Array<ApiEventHistory>;

  constructor(
    private caseService: CaseService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseClaims(this.caseId).subscribe(x => {
      this.claims = x;
      this.route.paramMap.subscribe(params => {
        if (this.claims.length > 0) {
          const claimId = Number(params.get('entity_id') ?? this.claims[0].claimId);
          const tab = params.get('case_tab');
          if (tab === 'claims') {
            this.onSelect(claimId);
          }
        }
      });
    });
  }

  // tslint:disable-next-line:no-any
  partyName(party: any): string {
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name;
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName;
    }
  }

  claimantName(claim: Claim): string {
    return this.partyName(claim.parties.claimants[0]);
  }

  claimName(claim: Claim): string {
    return this.partyName(claim.parties.claimants[0])
      + (claim.parties.claimants.length > 1 ? ' et al' : '')
      + ' vs '
      + this.partyName(claim.parties.defendants[0])
      + (claim.parties.defendants.length > 1 ? ' et al' : '');
  }

  onSelect(claimId: number): void {
    this.selectedClaim = this.claims.find(x => x.claimId === claimId);
    this.caseService.getClaimEvents(claimId).subscribe(x => this.history = x);
    this.router.navigateByUrl(`/cases/${this.caseId}/claims/${claimId}`);
  }
}
