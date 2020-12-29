import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import {ClaimHistory} from '../../generated/client-lib/model/claimHistory';
import {
  CaseActions,
  CaseControllerService,
  CaseParty,
  CaseSearchResult,
  Claim,
  ClaimControllerService
} from '../../generated/client-lib';
import {CaseHistory} from '../../generated/client-lib/model/caseHistory';

@Injectable({
  providedIn: 'root'
})
export class CaseService {

  baseUrl = environment.baseUrl;

  constructor(
    private http: HttpClient,
    private claims: ClaimControllerService,
    private cases: CaseControllerService,
  ) {
  }

  searchCases(data: string): Observable<Array<CaseSearchResult>> {
    const query = btoa(data);
    return this.cases.searchCases(query);
  }

  public getCase(caseId: number): Observable<CaseActions> {
    return this.cases.getCase(caseId);
  }

  public getCaseClaims(caseId: number): Observable<Array<Claim>> {
    if (this.isTestEnv()) {
      return of([]);
    }
    return this.claims.getClaims(caseId);
  }

  public getClaimEvents(claimId: number): Observable<Array<ClaimHistory>> {
    if (this.isTestEnv()) {
      return of([]);
    }
    return this.claims.getClaimEvents(claimId);
  }

  public getCaseParties(caseId: number): Observable<Array<CaseParty>> {
    if (this.isTestEnv()) {
      return of([]);
    }
    return this.cases.getParties(caseId);
  }

  public getCaseEvents(caseId: number): Observable<Array<CaseHistory>> {
    // TODO - find alternative to assets folder that supports nesting.
    if (this.isTestEnv()) {
      return of([]);
    }
    return this.cases.getCaseEvents(caseId);
  }

  private isTestEnv(): boolean {
    return environment.baseUrl === '/assets/';
  }

}
