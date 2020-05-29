
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

/**
 * A collection of string-processing helpers.
 */
export class FormatHelper {

  /**
   * Left pads the string with zeros until it is the specified length
   * @param s
   * @param l
   */
  public static padZeros(s:string, length:number){
    while (s.length < length){
      s = '0' + s;
    }
    return s;
  }


  public static formatDateQueryParam(d:Date){
    if (d==null) return '0000-00-00';
    let s:string = d.getFullYear().toString();
    s += '-';
    s += FormatHelper.padZeros((d.getMonth()+1).toString(), 2);
    s += '-';
    s += FormatHelper.padZeros(d.getDate().toString(), 2);
    return s;
  }

  static removeCSVQuotes(field: string) {
    field=field.trim();
    if (field.length > 0) {

      //Editing a csv in excel can sometimes cause the quotes to be wrapped multiple times.
      //we repeat until we no longer have surrounding quotes.
      while (field.length > 0 &&  (field[0] == '"' && field[field.length - 1] == '"')) {
        console.error("QUOTE FOUND: " + field);
        field = field.substring(1, (field.length - 1));
        console.error("AFTER: " + field);
      }

    }
    return field;
  }

  /***
   * Converts a date in MM/DD/YYYY format to the YYYYMMDD epoch date format
   * @param dateValue
   */
  static convertToEnergyDate(dateValue:string):number{
    let v:Array<string> = dateValue.split('/');
    let s=this.padZeros(v[2],4);
    s+=this.padZeros(v[0],2);
    s+=this.padZeros(v[1],2);
    return parseInt(s);
  }
}
