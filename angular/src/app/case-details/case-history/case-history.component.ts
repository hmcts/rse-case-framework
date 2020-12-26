import {Component, Input, OnInit} from '@angular/core';
import {CaseHistory} from '../../../generated/client-lib/model/caseHistory';

@Component({
  selector: 'app-case-history',
  templateUrl: './case-history.component.html',
  styleUrls: ['./case-history.component.scss']
})
export class CaseHistoryComponent implements OnInit {

  @Input() history: Array<CaseHistory>;
  eventDescriptions = {
    CreateClaim: 'Case opened',
    ClaimIssued: 'Claim Issued',
    AddClaim: 'New claim created',
    CloseCase: 'Case closed',
    AddParty: 'Party added',
    SubmitAppeal: 'Appeal submitted',
    ImportCitizens: 'Citizen details bulk imported',
    PurgeInactiveCitizens: 'Inactive citizen accounts removed',
    ConfirmService: 'Service confirmed',
  };
  constructor() { }

  ngOnInit(): void {
  }

}
