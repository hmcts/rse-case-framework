import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  baseUrl = environment.baseUrl;

  user = new FormControl();
  password = new FormControl();
  form: FormGroup = new FormGroup({
    user: this.user,
    password: this.password,
  });
  constructor(
    private http: HttpClient,
    private router: Router,
  ) { }

  ngOnInit(): void {
  }

  onLogin(): void {
    const formData = new FormData();
    formData.append('username', this.user.value);
    formData.append('password', this.password.value);
    this.http.post(this.baseUrl + '/perform_login', formData, { withCredentials: true, responseType: 'text'}).subscribe(
      (response) => this.router.navigateByUrl('/', { replaceUrl: true}),
      (response) => console.error(response),
    );
  }
}
