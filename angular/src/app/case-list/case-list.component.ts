import { Component, OnInit, Input } from '@angular/core';
import {CaseSearchResult} from '../../generated/client-lib';

@Component({
  selector: 'app-case-list',
  templateUrl: './case-list.component.html',
  styleUrls: ['./case-list.component.scss']
})
export class CaseListComponent implements OnInit {

  constructor() { }
  @Input() caseList: Array<CaseSearchResult>;
  stateNames: { [k in CaseSearchResult.StateEnum]: string} = {
    Created: $localize`Created`,
    Stayed: $localize`Stayed`,
    Closed: $localize`Closed`,
  };
  ngOnInit(): void {
  }
}
