import { Component, OnInit } from '@angular/core';
import {UserControllerService, UserInfo} from '../../../generated/client-lib';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  user: UserInfo;

  constructor(private userService: UserControllerService) { }

  ngOnInit(): void {
    this.userService.getUserInfo().subscribe(
      user => this.user = user
    );
  }

}
