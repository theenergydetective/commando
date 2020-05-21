import {Component} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NGXLogger} from 'ngx-logger';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {ConfirmDialogComponent} from "../confirm-dialog";
import {MatDialog} from "@angular/material/dialog";

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
              public dialog: MatDialog,
              private logger: NGXLogger) {

  }


  onLogout() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Logout',
        body: 'Are you sure you want to log out?'
      }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result != null && result) {
        this.authService.logOut();
        this.router.navigate(['/login']);
      }
    });

  }

  onEditClick() {

  }
}
