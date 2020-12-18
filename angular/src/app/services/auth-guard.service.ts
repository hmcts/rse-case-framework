import { Injectable } from '@angular/core';
import {CanActivate } from '@angular/router';
import {UserControllerService} from '../../generated/client-lib';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private userService: UserControllerService) { }

  canActivate(): Promise<boolean> {
    return new Promise((resolve) => {
      this.userService.getUserInfo().subscribe(
        user => resolve(user != null),
        error => window.location.href = '/oauth2/authorization/idam');
    });
  }
}
