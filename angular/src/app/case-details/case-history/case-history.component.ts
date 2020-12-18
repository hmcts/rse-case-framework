import {Component, Input, OnInit} from '@angular/core';
import {ApiEventHistory} from '../../../generated/client-lib';

@Component({
  selector: 'app-case-history',
  templateUrl: './case-history.component.html',
  styleUrls: ['./case-history.component.scss']
})
export class CaseHistoryComponent implements OnInit {

  @Input() history: Array<ApiEventHistory>;
  eventDescriptions = {
    CreateClaim: 'Case opened',
    ClaimIssued: 'Claim Issued',
    AddNotes: 'Case notes added',
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
