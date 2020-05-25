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

export class MonthYearRange {
  public start:MonthYear = null;
  public end:MonthYear = null;
}

export class MonthYear {
  public month:number;
  public year:number;
  public selected:boolean = false;

  /***
   * Initializes the Month year using the specified date.
   * @param date
   * @param selected
   */
  constructor(date:Date = new Date(), selected: boolean = false) {
    this.month = date.getMonth();
    this.year = date.getFullYear();
    this.selected = selected;
  }

  public compare(o:MonthYear):number{
    if (o == null) return 1;
    let thisValue = (this.year *100) + this.month;
    let thatValue = (o.year * 100) + o.month;
    if (thisValue < thatValue) return -1;
    else if (thisValue == thatValue) return 0;
    else return 1;
  }

  public isInBetween(startDate:MonthYear, endDate:MonthYear):boolean{
    return  (this.compare(startDate) >= 0) && (this.compare(endDate) <= 0)
  }
}

