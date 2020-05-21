import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {NGXLogger} from 'ngx-logger';
import {DomSanitizer} from '@angular/platform-browser';
import {MatIconRegistry} from "@angular/material/icon";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Commando';

  constructor(
    private router: Router,
    private logger: NGXLogger,
    private iconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
  ) {

    // TODO:Register icons used by the app if needed
    iconRegistry.addSvgIcon('warning', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/warning-24px.svg'));
    iconRegistry.addSvgIcon('close', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/close-24px.svg'));


  }

}
