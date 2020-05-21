import {ComponentFixture, TestBed} from '@angular/core/testing';
import {LoggerConfig, NGXLogger, NGXLoggerHttpService, NgxLoggerLevel, NGXMapperService} from 'ngx-logger';
import {NGXLoggerHttpServiceMock, NGXMapperServiceMock} from 'ngx-logger/testing';
import {AuthService} from '../../services/auth.service';

import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {MatCardModule} from '@angular/material/card';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIcon, MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {LayoutModule} from '@angular/cdk/layout';
import {FlexLayoutModule} from '@angular/flex-layout';
import {HttpClientModule} from '@angular/common/http';
import {Component, Input} from '@angular/core';
import {AdminComponent} from "./admin.component";
import {DatePipe} from "@angular/common";
import {MatSelectModule} from "@angular/material/select";
import {MockAuthService} from "../../mocks/mock-auth-service";



describe('AdminComponent', () => {

  @Component({
    selector: 'mat-icon',
    template: '<span></span>'
  })
  class MockMatIconComponent {
    @Input() svgIcon: any;
    @Input() fontSet: any;
    @Input() fontIcon: any;
  }

  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;


  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminComponent],
      imports: [
        // LoggerTestingModule
        HttpClientModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatSelectModule,
        FormsModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        LayoutModule

      ],
      providers: [
        DatePipe,
        NGXLogger,
        FormBuilder,
        {provide: AuthService, useClass: MockAuthService},
        {provide: NGXLoggerHttpService, useClass: NGXLoggerHttpServiceMock},
        {provide: NGXMapperService, useClass: NGXMapperServiceMock},
        {provide: LoggerConfig, useValue: {level: NgxLoggerLevel.TRACE}}
      ]
    })
      .overrideModule(MatIconModule, {
        remove: {
          declarations: [MatIcon],
          exports: [MatIcon]
        },
        add: {
          declarations: [MockMatIconComponent],
          exports: [MockMatIconComponent]
        }
      });


    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    // Inject the mocks for testing
    // httpClient = TestBed.get(HttpClient);
    // httpTestingController = TestBed.get(HttpTestingController);

  });


  it('should compile', () => {
    expect(component).toBeTruthy();
  });

});
