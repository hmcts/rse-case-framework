import {Component, Input, OnInit} from '@angular/core';
import {CaseService} from "../../../case-service.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-claims-tab',
  templateUrl: './claims-tab.component.html',
  styleUrls: ['./claims-tab.component.scss']
})
export class ClaimsTabComponent implements OnInit {
  claims: Array<any>;
  parties: Array<any>;
  caseId: string;

  constructor(
    private caseService: CaseService,
    private route: ActivatedRoute,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.caseId = this.route.snapshot.paramMap.get('id') ?? '1'
    this.caseService.getCase(this.caseId).subscribe(x => {
      this.claims = x.data.claims
      this.parties = x.data.parties
    })
  }

  partyName(partyId: number) : string {
    const party = this.parties[partyId - 1];
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName
    }
  }

  addClaim() {
    this.router.navigateByUrl('/cases/' + this.caseId + '/create-event?id=AddClaim');
  }

  claimName(claim: any) {
    return this.partyName(claim.claimantIds[0])
      + (claim.claimantIds.length > 1 ? " et al" : "")
      + " vs "
      + this.partyName(claim.defendantIds[0])
      + (claim.defendantIds.length > 1 ? " et al" : "")
  }
}
