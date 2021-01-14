import {Component, Input, OnInit} from '@angular/core';
import {CaseHistory} from '../../../generated/client-lib';
import {AllCaseEvents} from '../../events/events';

@Component({
  selector: 'app-case-history',
  templateUrl: './case-history.component.html',
  styleUrls: ['./case-history.component.scss']
})
export class CaseHistoryComponent {

  @Input() history: Array<CaseHistory>;
  eventDescriptions: { [k in AllCaseEvents ]: string } = {
    CreateClaim: $localize`Case opened`,
    ClaimIssued: $localize`Claim Issued`,
    AddClaim: $localize`New claim created`,
    CloseCase: $localize`Case closed`,
    AddParty: $localize`Party added`,
    SubmitAppeal: $localize`Appeal submitted`,
    ImportCitizens: $localize`Citizen details bulk imported`,
    PurgeInactiveCitizens: $localize`Inactive citizen accounts removed`,
    ConfirmService: $localize`Service confirmed`,
    ServiceAcknowledged: $localize`Service acknowledged`,
    ResponseFiled: $localize`Response filed`,
  };
  constructor() { }

  getDescription(id: AllCaseEvents): string {
    return this.eventDescriptions[id];
  }
}
