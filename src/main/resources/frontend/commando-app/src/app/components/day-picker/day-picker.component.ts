/*
 * Copyright (c) 2020. Energy, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

import {AfterContentInit, Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {NGXLogger} from "ngx-logger";
import {BillingDate, BillingDateRange} from "../../models/billing-date";
import * as moment from 'moment';
import {FormatHelper} from "../../helpers/format-helper";
import {DaterangepickerComponent} from "ngx-daterangepicker-material";

@Component({
  selector: 'day-picker',
  templateUrl: './day-picker.component.html',
  styleUrls: ['./day-picker.component.scss']
})
export class DayPickerComponent implements AfterContentInit{
  @Output() selected: EventEmitter<BillingDateRange> = new EventEmitter();

  constructor(private logger: NGXLogger) {
  }

  private static TODAY_STRING:string = new Date().getFullYear()  +
    '-' + FormatHelper.padZeros((new Date().getMonth()+1)+'', 2) +
    '-' + FormatHelper.padZeros((new Date().getDate()+1)+'', 2) +
    'T00:00Z';

  defaultDate = {
    chosenLabel:'',
    startDate: moment(DayPickerComponent.TODAY_STRING),
    endDate: moment(DayPickerComponent.TODAY_STRING)
  };

  ngAfterContentInit(): void {
    //Default to today.
    this.chosenDate(this.defaultDate);

  }





  chosenDate(chosenDate: { chosenLabel: string; startDate: moment.Moment; endDate: moment.Moment }): void {
    let range:BillingDateRange = new BillingDateRange();
    range.start = new BillingDate();
    range.end = new BillingDate();

    range.start.year = chosenDate.startDate.year();
    range.start.month = chosenDate.startDate.month();
    range.start.date = chosenDate.startDate.date();
    if (chosenDate.endDate == null){
      range.end.year = chosenDate.startDate.year();
      range.end.month = chosenDate.startDate.month();
      range.end.date = chosenDate.startDate.date();
    } else {
      range.end.year = chosenDate.endDate.year();
      range.end.month = chosenDate.endDate.month();
      range.end.date = chosenDate.endDate.date();
    }

    this.logger.debug("[chosenDate] " + JSON.stringify(range, null, 2));
    this.selected.emit(range);
  }


  startDateChanged(chosenDate: { startDate: moment.Moment }) {
    let range:BillingDateRange = new BillingDateRange();
    range.start = new BillingDate();
    range.end = new BillingDate();
    range.start.year = chosenDate.startDate.year();
    range.start.month = chosenDate.startDate.month();
    range.start.date = chosenDate.startDate.date();
    range.end.year = chosenDate.startDate.year();
    range.end.month = chosenDate.startDate.month();
    range.end.date = chosenDate.startDate.date();

    this.logger.debug("[startDateChanged] " + JSON.stringify(range, null, 2));
    this.selected.emit(range);
  }
}
