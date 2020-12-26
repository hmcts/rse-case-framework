import {EventsBuilder} from '../forms/components/stepper/linear-stepper/types';
import {ConfirmServiceComponent} from '../forms/components/steps/confirm-service/confirm-service.component';
import {ConfirmServiceAnswersComponent} from '../forms/components/steps/confirm-service/confirm-service-answers.component';

export class ClaimEvents {
   public static readonly EVENTS = new EventsBuilder()
    .event('ConfirmService')
      .atLocation('claims')
      .redirectToTab('claims')
      .customPage(ConfirmServiceComponent)
      .withAnswers(ConfirmServiceAnswersComponent)
      .buildPage()
    .buildEvent()
    .toMap();
}
