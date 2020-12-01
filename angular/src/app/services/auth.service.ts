import { Injectable } from '@angular/core';
import {HttpClient } from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user: any;

  constructor(
    private http: HttpClient,
  ) {
  }

  getUser(): Observable<any> {
    return this.http.get(environment.baseUrl + 'userInfo');
  }

}
