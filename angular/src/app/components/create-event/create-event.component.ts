import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Event, StepType} from '../../forms/components/stepper/linear-stepper/types';
import {EventList} from '../../events/events';
import {CaseControllerService} from '../../../generated/client-lib';
import {Utils} from '../../services/helper';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages: Array<StepType>;
  files = new FormData();

  caseId: string;
  private eventId: string;
  private entityId: string;
  private event: Event;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private caseService: CaseControllerService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(x => {
      const id = x.get('case_id');
      if (id) {
        this.caseId = Utils.caseId(id);
      }
    });
    this.route.queryParamMap.subscribe(x => {
      this.eventId = x.get('id') ?? 'CreateClaim';
      this.entityId = x.get('entity_id') ?? this.caseId;
      this.event = Utils.notNull(EventList.EVENTS.get(this.eventId));
      this.pages = this.event.steps;
    });
  }

  // tslint:disable-next-line:no-any
  onSubmit(data: any): void {
    const isFile = this.files.has('file');
    const payload = isFile
      ? this.files
      : {
        id: this.eventId,
        data,
      };
    let url = '/web/' + (this.event.location ?? 'cases');
    if (this.entityId) {
      url += '/' + this.entityId + (isFile
        ? '/files'
        : '/events');
    }

    this.http.post(this.baseUrl + url, payload, { observe: 'response' })
      .subscribe(resp => {
        const redirectTo = Utils.notNull(EventList.EVENTS.get(this.eventId)).redirectTo;

        if (redirectTo) {
          this.router.navigateByUrl(`/cases/${this.caseId}/${redirectTo}`, {replaceUrl: true});
        } else {
          this.router.navigateByUrl(Utils.notNull(resp.headers.get('location')), {replaceUrl: true});
        }
      });
  }
}
