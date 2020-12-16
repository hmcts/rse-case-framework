import { Injectable } from '@angular/core';
import {HttpClient } from "@angular/common/http";
import {Observable} from "rxjs";
import {UserControllerService} from "../../generated/client-lib";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user: any;

  constructor(
    private http: HttpClient,
    private userService: UserControllerService,
  ) {
  }

  getUser(): Observable<any> {
    return this.userService.getUserInfo();
  }

}
