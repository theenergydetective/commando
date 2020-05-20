import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserSession, UserSessionState} from '../../models/user-session';
import {Router} from '@angular/router';
import {MustMatch} from "../../helpers/must-match.validator";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})

export class AdminComponent implements OnInit, AfterViewInit, OnDestroy  {
  form: FormGroup;
  hideError = true;
  timezones:Array<string> = [];

  constructor(private authService: AuthService,
              private formBuilder: FormBuilder,
              private router: Router,
              private logger: NGXLogger) {

  }


  ngOnInit() {
    this.authService.logOut();
    this.form = this.formBuilder.group({
      username: [null, [Validators.required]],
      password: [null, Validators.required],
      confirmUsername: [null, [Validators.required]],
      confirmPassword: [null, Validators.required],
      activationKey: [''],
      timezone: [null, Validators.required],
    }, {
      validator: [
        MustMatch('password', 'confirmPassword'),
        MustMatch('username', 'confirmUsername'),
        ]
    });


  }


  ngAfterViewInit() {
    this.form.get('username').setValue('');
    this.form.get('password').setValue('');
    this.form.get('confirmUsername').setValue('');
    this.form.get('confirmPassword').setValue('');
    this.form.get('activationKey').setValue('');

    //this.form.get('timezone').setValue('America/New_York');
    this.authService.getTimeZones().then((tzList:Array<string>)=>{
      this.timezones = tzList;
      this.form.get('timezone').setValue('America/New_York');
    })

  }

  ngOnDestroy() {
  }


   submit() {
    const username = this.form.get('username').value;
    const password = this.form.get('password').value;
    const timezone = this.form.get('timezone').value;
    const activationKey = this.form.get('activationKey').value;



  }


  hideErrorBox() {
    this.hideError = true;
  }


}
