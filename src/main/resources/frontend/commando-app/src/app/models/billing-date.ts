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


import {FormatHelper} from "../helpers/format-helper";

export enum ExportType {
  DAY='DAY',
  BILLING='BILLING'
}

export class BillingDateRange {
  public start:BillingDate = null;
  public end:BillingDate = null;
}


export class BillingDate {
  public date: number = 1;
  public month:number;
  public year:number;
  public selected:boolean = false;

  /***
   * Initializes the Month year using the specified date.
   * @param date
   * @param selected
   */
  constructor(date:Date = new Date(),  selected: boolean = false) {
    this.month = date.getMonth();
    this.date = date.getDate();
    this.year = date.getFullYear();
    this.selected = selected;
  }

  public compare(o:BillingDate):number{
    if (o == null) return 1;
    let thisValue = (this.year *10000) + (this.month * 100) + this.date;
    let thatValue = (o.year *10000) + (o.month * 100) + o.date;
    if (thisValue < thatValue) return -1;
    else if (thisValue == thatValue) return 0;
    else return 1;
  }

  public isInBetween(startDate:BillingDate, endDate:BillingDate):boolean{
    return  (this.compare(startDate) >= 0) && (this.compare(endDate) <= 0)
  }

  public toSimpleDateString():string{
    let s:string = this.year.toString();
    s += '-';
    s += FormatHelper.padZeros((this.month+1).toString(), 2);
    s += '-';
    s += FormatHelper.padZeros((this.date).toString(), 2);
    return s;
  }

  /**
   * Creates a new Billing Date by adding a single month to it.
   * @param bd
   */
  static addMonth(bd:BillingDate):BillingDate{
    let newBD = new BillingDate();
    newBD.year = bd.year;
    newBD.month = bd.month;
    newBD.date = bd.date;

    newBD.month++;
    if (newBD.month >= 12) {
      newBD.month = 0;
      newBD.year++;
    }
    return newBD;
  }


  /**
   * Creates a new Billing Date by adding a single day
   * @param bd
   */
  static addDate(bd:BillingDate):BillingDate{
    let d:Date = new Date(bd.year, bd.month, bd.date, 0, 0 ,0,0);
    d.setDate(d.getDate() + 1);
    let newBD = new BillingDate();
    newBD.year = d.getFullYear();
    newBD.month = d.getMonth();
    newBD.date = d.getDate();
    return newBD;
  }


  /**
   * Creates a new billing date adjusted for the meter read date.
   * @param bd
   * @param meterReadDate
   */
  public static adjustForMeterReadDate(bd:BillingDate, meterReadDate:number):BillingDate {
    let newBD = new BillingDate();
    newBD.year = bd.year;
    newBD.month = bd.month;
    newBD.date = meterReadDate;
    let daysInMonth = new Date(newBD.year, newBD.month, 0).getDate();
    if (newBD.date > daysInMonth) newBD.date = daysInMonth;
    return newBD;
  }
}

