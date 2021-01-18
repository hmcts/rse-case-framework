import {Component, ViewEncapsulation, OnInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {CaseService} from '../../services/case-service.service';
import {CaseActions, CaseHistory} from '../../../generated/client-lib';
import {Utils} from '../../services/helper';
import ActionsEnum = CaseActions.ActionsEnum;


@Component({
  selector: 'app-case-view',
  templateUrl: './case-view.component.html',
  styleUrls: ['./case-view.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CaseViewComponent implements OnInit {
  caseId: string;
  case: CaseActions;
  events: Array<CaseHistory>;
  selectedValue: string;
  selectedIndex: number;
  tabs = [
    'history',
    'parties',
    'claims',
    'citizens',
  ];

  eventDescriptions: { [k in ActionsEnum]: string } = {
    CreateClaim: $localize`Create a claim`,
    CloseCase: $localize`Close the case`,
    AddParty: $localize`Add a party`,
    AddClaim: $localize`Create a new claim`,
    SubmitAppeal: $localize`Submit an appeal`,
    ImportCitizens: $localize`Import citizens`,
    PurgeInactiveCitizens: $localize`Purge inactive citizens`
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(x => {
      this.caseId = Utils.caseId(x.get('case_id'));
      const tab = Utils.notNull(x.get('case_tab'));
      this.selectedIndex = this.tabs.indexOf(tab);
      this.caseService.getCase(this.caseId).subscribe(result => {
        this.case = result;
        this.selectedValue = this.case.actions.values().next().value;
      });
      this.caseService.getCaseEvents(this.caseId).subscribe(result => {
        this.events = result;
      });
    });
  }

  actions(): Array<CaseActions.ActionsEnum> {
    return Array.from(this.case.actions.values()).sort();
  }

  // Update the address bar URL to track the active tab.
  onTabChange($event: number): void {
    const value = this.tabs[$event];
    const currentTab = this.route.snapshot.paramMap.get('case_tab');
    if (currentTab !== value) {
      this.router.navigateByUrl(`/cases/${this.caseId}/${value}`);
    }
  }
}
