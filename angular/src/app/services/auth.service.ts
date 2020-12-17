import { Injectable } from '@angular/core';
import {HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserControllerService} from '../../generated/client-lib';
import {environment} from '../../environments/environment';

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
    return this.http.get(environment.baseUrl + '/web/userInfo');
  }

}
