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

import {AfterContentInit, Component, EventEmitter, Input, Output} from '@angular/core';
import {NGXLogger} from "ngx-logger";
import {BillingRange, MonthYearRange} from "../../models/month-year";

class MonthPickerData {
  public year:number;
  public months:Array<BillingRange> = [];
}


@Component({
  selector: 'day-picker',
  templateUrl: './day-picker.component.html',
  styleUrls: ['./day-picker.component.scss']
})
export class DayPickerComponent implements AfterContentInit{

  @Input('start-year')
  public startYear:number = 2019;

  public values:Array<MonthPickerData> = [];

  private range:MonthYearRange = new MonthYearRange();


  @Output() selected: EventEmitter<MonthYearRange> = new EventEmitter();
//this.selected.emit(value);

  constructor(private logger: NGXLogger) {
  }

  ngAfterContentInit(): void {
    let thisYear = new Date().getFullYear();


    this.values = [];
    for (let y=this.startYear; y <= thisYear; y++){
      let value = new MonthPickerData();
      value.year = y;
      value.months = [];

      //Set up the month values
      for (let m=0; m< 12; m++) {
        let my:BillingRange = new BillingRange();
        my.month = m;
        my.year = y;
        my.selected = false;
        value.months.push(my);
      }

      this.values.push(value);
    }

    //Default to the current month
    this.onSelected(new BillingRange(new Date(),true));

  }


  onSelected($event: BillingRange) {
    if (this.range.start == null || this.range.end != null){
      //Clear the previous selection and start a new selection range.
      this.range.start = $event;
      this.range.end = null;

      //Clear All except first select
      for (let v=0; v < this.values.length; v++){
        let months:Array<BillingRange> = this.values[v].months;
        for (let m=0; m < months.length; m++){
          let my:BillingRange = months[m];
          my.selected = my.month == this.range.start.month && my.year == this.range.start.year;
        }
      }
    } else if (this.range.end == null){
      this.range.end = $event;

      //Swap the order
      if (this.range.start.compare(this.range.end) > 0){
        this.range.end = this.range.start;
        this.range.start = $event;
      }

      //Fill in the blanks
      for (let v=0; v < this.values.length; v++){
        let months:Array<BillingRange> = this.values[v].months;
        for (let m=0; m < months.length; m++){
          let my:BillingRange = months[m];
          my.selected = my.isInBetween(this.range.start, this.range.end);
        }
      }


    }

    //Send results upstream
    this.selected.emit(this.range);


  }
}
