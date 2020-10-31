import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CaseService {

  baseUrl = environment.baseUrl;

  constructor(
    private http: HttpClient
  ) {}

  searchCases(data) {
    const query = btoa(JSON.stringify(data));
    return this.http.get(this.baseUrl + '/api/search', {
      withCredentials: true,
      headers: new HttpHeaders({ 'search-query': query})
    });

  }

  public getCase(caseId: string): Observable<any> {
    return this.http.get(this.baseUrl + '/api/cases/' + caseId, {
      withCredentials: true,
    });
  }

  public getCaseEvents(caseId: string): Observable<any> {
    return this.http.get(this.baseUrl + '/api/cases/' + caseId + '/events', {
      withCredentials: true,
    });
  }

}
