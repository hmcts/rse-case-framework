import {Component, ViewEncapsulation, OnInit, Input} from '@angular/core';
import {Router, ActivatedRoute, Params} from '@angular/router';
import {CaseService} from "../../services/case-service.service";


@Component({
  selector: 'app-case-view',
  templateUrl: './case-view.component.html',
  styleUrls: ['./case-view.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CaseViewComponent implements OnInit {
  caseId: string;
  case: any;
  events: any = [];
  selectedValue: any;
  selectedIndex: number;
  tabMap = {
    history: 0,
    parties: 1,
    claims: 2,
    citizens:3,
  };

  eventDescriptions = {
    AddNotes: 'Add case notes',
    CloseCase: 'Close the case',
    AddParty: 'Add a party',
    AddClaim: 'Create a new claim',
    SubmitAppeal: 'Submit an appeal',
    ImportCitizens: 'Import citizens',
    PurgeInactiveCitizens: 'Purge inactive citizens',
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(x => {
      this.caseId = x.get('claim');
      this.caseService.getCase(this.caseId).subscribe(result => {
        this.case = result
        this.selectedValue = this.case.actions[0]
      });
      this.caseService.getCaseEvents(this.caseId).subscribe(result => {
        this.events = result;
      });
    });

    this.route.queryParamMap.subscribe(q => {
      const tab = q.get('tab');
      this.selectedIndex = this.tabMap[tab];
    });
  }

  actions() {
    return this.case.actions.sort()
  }

  // Update the address bar URL to track the active tab.
  onTabChange($event) {
    let value = 'history';
    for (const key in this.tabMap) {
      if (this.tabMap[key] == $event) {
        value = key;
        break;
      }
    }

    const queryParams: Params = { tab: value };
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: queryParams,
        queryParamsHandling: 'merge',
      });
  }
}
