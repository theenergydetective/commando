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
import {BillingRange} from "../../models/month-year";

@Component({
  selector: 'day-picker-toggle',
  templateUrl: './day-picker-toggle.component.html',
  styleUrls: ['./day-picker-toggle.component.scss']
})
export class DayPickerToggleComponent implements AfterContentInit{

  private static MONTHS:Array<string> = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

   @Input('month-year')
   public monthYear:BillingRange = new BillingRange();

   @Output() selected: EventEmitter<BillingRange> = new EventEmitter();

  label:string;


  constructor() {
  }

  ngAfterContentInit(): void {
    this.label = DayPickerToggleComponent.MONTHS[this.monthYear.month];
  }


  onClick() {
    this.monthYear.selected = !this.monthYear.selected;
    this.selected.emit(this.monthYear);
  }
}
