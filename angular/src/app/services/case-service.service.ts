import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from "rxjs";
import {environment} from "../../environments/environment";
import {
  ApiEventHistory,
  CaseActions,
  CaseControllerService, CaseParty,
  CaseSearchResult,
  ClaimControllerService
} from '../../generated/client-lib';

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

  searchCases(data): Observable<Array<CaseSearchResult>> {
    const query = btoa(JSON.stringify(data));
    return this.cases.searchCases(query);
  }

  public getCase(caseId: number): Observable<CaseActions> {
    return this.cases.getCase(caseId);
  }

  public getCaseClaims(caseId: number): Observable<any> {
    if (this.isTestEnv()) {
      return of([]);
    }
    return this.claims.getClaims(caseId);
  }

  public getClaimEvents(claimId: number): Observable<Array<ApiEventHistory>> {
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

  public getCaseEvents(caseId: number): Observable<Array<ApiEventHistory>> {
    // TODO - find alternative to assets folder that supports nesting.
    if (this.isTestEnv()) {
      return of([]);
    }
    return this.cases.getCaseEvents(caseId);
  }

  private isTestEnv(): boolean {
    return environment.baseUrl == '/assets/';
  }

}
