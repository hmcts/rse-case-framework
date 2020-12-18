import {Component, ViewEncapsulation, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {CaseService} from '../../services/case-service.service';
import {ApiEventHistory, CaseActions} from '../../../generated/client-lib';


@Component({
  selector: 'app-case-view',
  templateUrl: './case-view.component.html',
  styleUrls: ['./case-view.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CaseViewComponent implements OnInit {
  caseId: number;
  case: CaseActions;
  events: Array<ApiEventHistory>;
  selectedValue: string;
  selectedIndex: number;
  tabMap = {
    history: 0,
    parties: 1,
    claims: 2,
    citizens: 3,
  };

  eventDescriptions = {
    AddNotes: 'Add case notes',
    CloseCase: 'Close the case',
    AddParty: 'Add a party',
    AddClaim: 'Create a new claim',
    SubmitAppeal: 'Submit an appeal',
    ImportCitizens: 'Import citizens',
    PurgeInactiveCitizens: 'Purge inactive citizens',
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(x => {
      this.caseId = Number(x.get('case_id'));
      const tab = x.get('case_tab');
      this.selectedIndex = this.tabMap[tab];
      this.caseService.getCase(this.caseId).subscribe(result => {
        this.case = result;
        this.selectedValue = this.case.actions[0];
      });
      this.caseService.getCaseEvents(this.caseId).subscribe(result => {
        this.events = result;
      });
    });

  }

  actions(): Array<string> {
    return this.case.actions.sort();
  }

  // Update the address bar URL to track the active tab.
  onTabChange($event): void {
    let value = 'history';
    for (const key in this.tabMap) {
      if (this.tabMap[key] === $event) {
        value = key;
        break;
      }
    }

    const currentTab = this.route.snapshot.paramMap.get('case_tab');
    if (currentTab !== value) {
      this.router.navigateByUrl(`/cases/${this.caseId}/${value}`);
    }
  }
}
