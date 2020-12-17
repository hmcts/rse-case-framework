import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  user: any;

  constructor(private auth: AuthService) { }

  ngOnInit(): void {
    this.auth.getUser().subscribe(
      user => this.user = user
    );
  }

}
