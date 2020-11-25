import { Injectable } from '@angular/core';
import {HttpClient } from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user: any;

  constructor(
    private http: HttpClient,
  ) {
  }

  getUser(): Promise<any> {
    return new Promise((resolve) => {
      this.http.get(environment.baseUrl + '/api/userInfo', {
      }).subscribe(
        result => { this.user = result; resolve(this.user) } ,
        error => { resolve(null) })
    })
  }
}
