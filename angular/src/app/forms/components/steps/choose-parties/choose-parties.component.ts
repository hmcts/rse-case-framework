import {Component, Input, OnInit} from '@angular/core';
import {StepComponent} from '../../stepper/form-stepper/types';
import {FormControl, FormGroup} from '@angular/forms';
import {CaseService} from '../../../../services/case-service.service';
import {CaseParty} from '../../../../../generated/client-lib';

@Component({
  selector: 'app-choose-parties',
  templateUrl: './choose-parties.component.html',
  styleUrls: ['./choose-parties.component.scss']
})
export class ChoosePartiesComponent implements OnInit, StepComponent {

  constructor(
    private caseService: CaseService,
  ) { }
  caseParties: Array<CaseParty> = [];
  @Input() caseId = '1';
  @Input() parties: any;
  @Input() form: FormGroup = new FormGroup({});
  defendants = new FormGroup({});
  claimants = new FormGroup({});

  validate: boolean;

  ngOnInit(): void {
    this.caseService.getCaseParties(Number(this.caseId)).subscribe(caseParties => {
      this.caseParties = caseParties;
      this.claimants.reset();
      this.defendants.reset();
      for (const party of this.caseParties) {
        this.claimants.addControl(String(party.partyId), new FormControl(false));
        this.defendants.addControl(String(party.partyId), new FormControl(false));
      }
    });
    this.form.addControl('claimants', this.claimants);
    this.form.addControl('defendants', this.defendants);
  }

  isCheckBoxDisabled(isClaimant: boolean, partyId: number): boolean {
    const otherSide = isClaimant ? this.defendants : this.claimants;
    // Cannot be both a claimant and defendant.
    if (otherSide.controls[partyId].value) {
      return true;
    }
    // Can't have one sided case
    const ourSide = isClaimant ? this.claimants : this.defendants;
    if (this.countParties(ourSide) == this.caseParties.length - 1 && !ourSide.controls[partyId].value) {
      return true;
    }
    return false;
  }

  countParties(side: FormGroup): number {
    let count = 0;
    for (const party of this.caseParties) {
      if (side.controls[party.partyId.toString()].value)  {
        count++;
      }
    }
    return count;
  }

  valid(): boolean {
    return this.countParties(this.claimants) > 0 && this.countParties(this.defendants) > 0;
  }

  partyName(party: any): string {
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name;
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName;
    }
  }
}
