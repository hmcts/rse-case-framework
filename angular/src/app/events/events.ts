import {CaseEvents} from './case-events';
import {ClaimEvents} from './claim-events';
import {CaseActions, ClaimHistory} from '../../generated/client-lib';

export type AllCaseEvents = CaseActions.ActionsEnum | ClaimHistory.IdEnum;
export class EventList {

  public static readonly EVENTS =
    new Map([...CaseEvents.EVENTS, ...ClaimEvents.EVENTS]);
}
