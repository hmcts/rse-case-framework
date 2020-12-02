import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CaseService {

  baseUrl = environment.baseUrl;

  constructor(
    private http: HttpClient
  ) {
  }

  searchCases(data): Observable<object> {
    const query = btoa(JSON.stringify(data));
    return this.http.get(this.baseUrl + 'search', {
      withCredentials: true,
      headers: new HttpHeaders({'search-query': query})
    });
  }

  public getCase(caseId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}cases/${caseId}`);
  }

  public getCaseClaims(caseId: string): Observable<any> {
    if (this.isTestEnv()) {
      return of([])
    }
    return this.http.get(`${this.baseUrl}cases/${caseId}/claims`);
  }

  public getCaseParties(caseId: string): Observable<any> {
    if (this.isTestEnv()) {
      return of([])
    }
    return this.http.get(`${this.baseUrl}cases/${caseId}/parties`);
  }

  public getCaseEvents(caseId: string): Observable<any> {
    // TODO - find alternative to assets folder that supports nesting.
    if (this.isTestEnv()) {
      return of([])
    }
    return this.http.get(this.baseUrl + 'cases/' + caseId + '/events', {
      withCredentials: true,
    });
  }

  private isTestEnv(): boolean {
    return environment.baseUrl == '/assets/web/';
  }

}
