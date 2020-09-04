import {Component, Input, OnInit} from '@angular/core';
import Questions from '../../../assets/schema/schema.json';

@Component({
  selector: 'app-case-history',
  templateUrl: './case-history.component.html',
  styleUrls: ['./case-history.component.scss']
})
export class CaseHistoryComponent implements OnInit {

  @Input() events: any;
  schema: any = Questions;
  constructor() { }

  ngOnInit(): void {
  }

}
