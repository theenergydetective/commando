import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserSession, UserSessionState} from '../../models/user-session';
import {Router} from '@angular/router';
import {AdminRequest} from "../../models/admin-request";

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.scss']
})

export class LogInComponent implements OnInit, AfterViewInit, OnDestroy  {
  form: FormGroup;
  hideError = true;

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
    });
  }


  ngAfterViewInit() {
    this.form.get('username').setValue('');
    this.form.get('password').setValue('');

    this.authService.getAdminRequest().then((ar:AdminRequest)=>{
      if (ar.adminSetup){
        //Force Redirect to admin page
        this.router.navigate(['/admin']);
      }
    });
  }

  ngOnDestroy() {
  }

   submit() {
    const username = this.form.get('username').value;
    const password = this.form.get('password').value;
    this.hideError = true;
    this.logger.info('[submit] Performing login: username: ' + username);
    this.authService.login(username, password)
      .then((userSession: UserSession) => {
        this.logger.debug('[submit] Login Result: ' + JSON.stringify(userSession, null, 2));
        switch (userSession.state) {
          case UserSessionState.AUTHENTICATED:{
            this.hideError = true;
            this.router.navigate(['/']);
            break;
          }
          case UserSessionState.AUTH_FAILED:{
            // Trigger invalid username/password error
            this.logger.warn('[submit] Invalid credentials');
            this.hideError = false;
            break;
          }
        }
      });
  }


  hideErrorBox() {
    this.hideError = true;
  }

}
