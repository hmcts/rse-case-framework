import {Component, EventEmitter, OnInit} from '@angular/core';
import {StepComponent} from '../../stepper/linear-stepper/types';
import {FormControl, FormGroup} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {environment} from '../../../../../environments/environment';
import {CitizenControllerService} from '../../../../../generated/client-lib';

@Component({
  selector: 'app-purge-inactive',
  templateUrl: './purge-inactive.component.html',
  styleUrls: ['./purge-inactive.component.scss']
})
export class PurgeInactiveComponent implements OnInit, StepComponent {

  constructor(
    private citizens: CitizenControllerService,
    private route: ActivatedRoute,
  ) { }

  baseUrl = environment.baseUrl;
  inactiveCount: number;

  form = new FormGroup({});

  validate: boolean;

  ngOnInit(): void {
    this.form.addControl('inactive_count', new FormControl());
    const id = this.route.snapshot.paramMap.get('id');
    if (null != id) {
      this.citizens.countInactive(id).subscribe(result => {
        this.inactiveCount = result;
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
