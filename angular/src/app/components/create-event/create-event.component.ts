import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {StepType} from "../../forms/components/stepper/form-stepper/types";
import {EventList} from "../../events/events";

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages: Array<StepType>;
  files = new FormData()

  caseId: string;
  private eventId: string;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.eventId = this.route.snapshot.queryParamMap.get('id');
    this.route.paramMap.subscribe(x => this.caseId = x.get('id'))
    if (null == this.eventId) {
      this.eventId = "CreateClaim";
    }
    this.pages = EventList.EVENTS.get(this.eventId).steps
  }


  onSubmit(data): void {
    const isFile = this.files.has('file')
    const payload = isFile
      ? this.files
      : {
        id: this.eventId,
        data: data,
      };
    let url = 'cases';
    if (this.caseId) {
      url += '/' + this.caseId + (isFile
        ? '/files'
        : '/events')
    }
    this.http.post(this.baseUrl + url, payload, { observe: 'response' , withCredentials: true })
      .subscribe(resp => {
        const redirectTo = EventList.EVENTS.get(this.eventId).redirectTo;

        if (redirectTo) {
          this.router.navigateByUrl(`/cases/${this.caseId}?tab=${redirectTo}`, {replaceUrl: true})
        } else {
          this.router.navigateByUrl(resp.headers.get('location'), {replaceUrl: true})
        }
      });
  }

}
