import {Component, EventEmitter, OnInit} from '@angular/core';
import {StepComponent} from '../../stepper/form-stepper/types';
import {FormControl, FormGroup} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../../../../environments/environment';

@Component({
  selector: 'app-purge-inactive',
  templateUrl: './purge-inactive.component.html',
  styleUrls: ['./purge-inactive.component.scss']
})
export class PurgeInactiveComponent implements OnInit, StepComponent {

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
  ) { }

  baseUrl = environment.baseUrl;
  inactiveCount: any;

  form = new FormGroup({});

  validate: boolean;

  ngOnInit(): void {
    this.form.addControl('inactive_count', new FormControl());
    const id = this.route.snapshot.paramMap.get('id');
    if (null != id) {
      this.http.get(this.baseUrl + `cases/${id}/citizens/inactive`).subscribe(result => {
        this.inactiveCount = result.inactive_count;
        this.form.patchValue({
          inactive_count: this.inactiveCount
        });
      });
    }
  }

  valid(): boolean {
    return true;
  }

}
