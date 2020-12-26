import {CaseEvents} from './case-events';
import {ClaimEvents} from './claim-events';

export class EventList {

  public static readonly EVENTS =
    new Map([...CaseEvents.EVENTS, ...ClaimEvents.EVENTS]);
}
