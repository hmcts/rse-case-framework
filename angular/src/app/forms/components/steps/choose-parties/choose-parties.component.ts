import {Component, Input, OnInit} from '@angular/core';
import {StepComponent} from "../../stepper/form-stepper/types";
import {FormControl, FormGroup} from "@angular/forms";
import {CaseService} from "../../../../services/case-service.service";

@Component({
  selector: 'app-choose-parties',
  templateUrl: './choose-parties.component.html',
  styleUrls: ['./choose-parties.component.scss']
})
export class ChoosePartiesComponent implements OnInit, StepComponent {
  caseParties: any = [];
  @Input() caseId = '1';
  @Input() parties: any;
  @Input() form: FormGroup = new FormGroup({})
  defendants = new FormGroup({})
  claimants = new FormGroup({})

  constructor(
    private caseService: CaseService,
  ) { }

  ngOnInit(): void {
    this.caseService.getCaseParties(Number(this.caseId)).subscribe(caseParties => {
      this.caseParties = caseParties
      this.claimants.reset()
      this.defendants.reset()
      for (let party of this.caseParties) {
        this.claimants.addControl(party.party_id, new FormControl(false))
        this.defendants.addControl(party.party_id, new FormControl(false))
      }
    });
    this.form.addControl('claimants', this.claimants)
    this.form.addControl('defendants', this.defendants)
  }

  isCheckBoxDisabled(isClaimant: boolean, partyId: string): boolean {
    const otherSide = isClaimant ? this.defendants : this.claimants;
    // Canot be both a claimant and defendant.
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
    for (let party of this.caseParties) {
      if (side.controls[party.party_id.toString()].value)  {
        count++;
      }
    }
    return count;
  }

  valid(): boolean {
    return this.countParties(this.claimants) > 0 && this.countParties(this.defendants) > 0;
  }

  validate: boolean;

  partyName(party: any) : string {
    switch (party.partyType) {
      case 'Company':
      case 'Organisation':
        return party.name
      default:
        return party.title + ' ' + party.firstName + ' ' + party.lastName
    }
  }
}
