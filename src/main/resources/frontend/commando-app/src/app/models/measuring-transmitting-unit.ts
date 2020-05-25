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

export class MeasuringTransmittingUnit {
  public id:string;
  public name:string;
  public rate:number = 0.1;
  public created: number = 0;
  public lastValue:number =0.0;
  public lastPost: number = 0;
  public lastDayValue:number =0.0;
  public lastDayPost: number = 0;
  public selected:boolean =false;
  public enabled:boolean=true;
  public timezone:string = 'America/New_York';
}

