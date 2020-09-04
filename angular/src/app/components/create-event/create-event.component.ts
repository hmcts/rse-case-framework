import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import Questions from '../../../assets/schema/schema.json';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Question} from '../../forms/dynamic-form/dynamic-form.component';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  baseUrl = environment.baseUrl;
  questionMap: Map<string, Array<Question>> = new Map([
    ['AddNotes', [{id: 'notes', type: 'text', title: 'Enter notes'}]],
    ['CloseCase', [{id: 'reason', type: 'text', title: 'Reason for closure'}]],
  ]);
  questions: Array<Question>;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
  ) { }

  ngOnInit(): void {
    let id = this.route.snapshot.queryParamMap.get('id');
    if (null != id) {
      this.questions = this.questionMap.get(id)
    }
  }


  onSubmit(data): void {
    const caseId = this.route.snapshot.paramMap.get('id');
    const eventId = this.route.snapshot.queryParamMap.get('id');
    const payload = {
id: eventId,
    data: data,
  };
    this.http.post(this.baseUrl + '/api/cases/' + caseId + '/events', payload, { observe: 'response' , withCredentials: true })
      .subscribe(resp => {
        this.router.navigateByUrl(resp.headers.get('location'), { replaceUrl: true })
      });
  }

}
