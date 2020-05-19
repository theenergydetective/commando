import {Component} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})

export class HomeComponent  {
  form: FormGroup;
  hideError = true;

  constructor(private authService: AuthService,
              private formBuilder: FormBuilder,
              private router: Router,
              private logger: NGXLogger) {

  }




}
