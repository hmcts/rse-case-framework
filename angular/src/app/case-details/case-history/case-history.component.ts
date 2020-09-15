import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-case-history',
  templateUrl: './case-history.component.html',
  styleUrls: ['./case-history.component.scss']
})
export class CaseHistoryComponent implements OnInit {

  @Input() history: any;
  eventDescriptions = {
    CreateClaim: 'Case opened',
    AddNotes: 'Case notes added',
    CloseCase: 'Case closed',
    AddParty: 'Party added',
    SubmitAppeal: 'Appeal submitted',
  }
  constructor() { }

  ngOnInit(): void {
  }

}
