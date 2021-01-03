import {Component, Input, OnInit} from '@angular/core';
import {CaseHistory} from '../../../generated/client-lib';
import {AllCaseEvents} from '../../events/events';

@Component({
  selector: 'app-case-history',
  templateUrl: './case-history.component.html',
  styleUrls: ['./case-history.component.scss']
})
export class CaseHistoryComponent implements OnInit {

  @Input() history: Array<CaseHistory>;
  eventDescriptions: { [k in AllCaseEvents ]: string } = {
    CreateClaim: 'Case opened',
    ClaimIssued: 'Claim Issued',
    AddClaim: 'New claim created',
    CloseCase: 'Case closed',
    AddParty: 'Party added',
    SubmitAppeal: 'Appeal submitted',
    ImportCitizens: 'Citizen details bulk imported',
    PurgeInactiveCitizens: 'Inactive citizen accounts removed',
    ConfirmService: 'Service confirmed',
    ServiceAcknowledged: 'Service acknowledged',
    ResponseFiled: 'Response filed',
  };
  constructor() { }

  ngOnInit(): void {
  }

  // tslint:disable-next-line:no-any
  getDescription(id: any): string {
    // @ts-ignore
    return this.eventDescriptions[id];
  }
}
