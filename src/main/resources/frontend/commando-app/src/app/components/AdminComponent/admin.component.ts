import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserSession, UserSessionState} from '../../models/user-session';
import {Router} from '@angular/router';
import {MustMatch} from "../../helpers/must-match.validator";
import {AdminRequest} from "../../models/admin-request";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})

export class AdminComponent implements OnInit, AfterViewInit, OnDestroy  {
  form: FormGroup;
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
      domain: [null, Validators.required],
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
    this.form.get('domain').setValue('');

    //this.form.get('timezone').setValue('America/New_York');
    this.authService.getTimeZones().then((tzList:Array<string>)=>{
      this.timezones = tzList;
      this.authService.getAdminRequest().then((ar:AdminRequest)=>{
        this.form.get('timezone').setValue(ar.timezone);
        this.form.get('domain').setValue(ar.domain);
        this.form.get('username').setValue(ar.username);
        this.form.get('confirmUsername').setValue(ar.username);
        this.form.get('activationKey').setValue(ar.activationKey);
      });
    })

  }

  ngOnDestroy() {
  }

   submit() {
    let adminRequest:AdminRequest = new AdminRequest();
    adminRequest.username = this.form.get('username').value;
    adminRequest.password = this.form.get('password').value;
    adminRequest.timezone = this.form.get('timezone').value;
    adminRequest.activationKey = this.form.get('activationKey').value;
    adminRequest.domain = this.form.get('domain').value;
    this.authService.postAdminRequest(adminRequest)
      .then(ar=>{
        this.router.navigate(['/login']);
      });
  }


}
