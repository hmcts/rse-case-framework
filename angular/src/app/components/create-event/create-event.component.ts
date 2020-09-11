import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Question} from '../../forms/dynamic-form/dynamic-form.component';
import {StepBuilder, StepType} from "../../forms/components/stepper/form-stepper/types";

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  baseUrl = environment.baseUrl;
  pages: Array<StepType>;
  questionMap: Map<string, Array<Question>> = new Map([
    ['AddNotes', [{id: 'notes', type: 'text', title: 'Enter notes'}]],
    ['CloseCase', [{id: 'reason', type: 'text', title: 'Reason for closure'}]],
    ['SubmitAppeal', [{id: 'reason', type: 'text', title: 'New evidence'}]],
  ]);
  questions: Array<Question>;
  private caseId: string;
  private eventId: string;


  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.eventId = this.route.snapshot.queryParamMap.get('id');
    this.caseId = this.route.snapshot.paramMap.get('id');
    if (null != this.eventId) {
      this.questions = this.questionMap.get(this.eventId)
    }
    this.pages = new StepBuilder()
      .dynamicPage('Event')
        .questions(this.questions)
        .build()
      .build();
  }


  onSubmit(data): void {
    const payload = {
      id: this.eventId,
      data: data,
    };
    this.http.post(this.baseUrl + '/api/cases/' + this.caseId + '/events', payload, { observe: 'response' , withCredentials: true })
      .subscribe(resp => {
        this.router.navigateByUrl(resp.headers.get('location'), { replaceUrl: true })
      });
  }

}
