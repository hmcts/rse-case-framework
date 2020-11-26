import { Injectable } from '@angular/core';
import {CanActivate } from "@angular/router";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private authService: AuthService) { }

  canActivate(): Promise<boolean> {
    return new Promise((resolve) => {
      this.authService.getUser()
        .then(function (user) {
          resolve(user != null)
          if (user == null) {
            window.location.href = "/oauth2/authorization/idam"
          }
        })
    });
  }
}
