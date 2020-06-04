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

import {AfterContentInit, Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {MeasuringTransmittingUnit} from "../../models/measuring-transmitting-unit";
import {FormatHelper} from "../../helpers/format-helper";
import {MtuService} from "../../services/mtu.service";
import {DailyEnergyService} from "../../services/daily-energy.service";
import {DailyEnergyData} from "../../models/daily-energy-data";


@Component({
  selector: 'import-data-dialog',
  templateUrl: './import-data-dialog.component.html',
  styleUrls: ['./import-data-dialog.component.scss']
})
export class ImportDataDialogComponent implements AfterContentInit{
  hasFile: boolean = false;
  private file:File = null;
  isUploading: boolean = false;
  progress: number = 0;

  constructor(
    private dailyEnergyService:DailyEnergyService,
    public dialogRef: MatDialogRef<ImportDataDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    dialogRef.disableClose = true;
  }



  onCloseClick(): void {
    this.dialogRef.close(false);
  }

  onClickImport() {
    if (this.hasFile){
      this.isUploading = true;
      let reader = new FileReader();

      reader.onload = () => {
        // this 'text' is the content of the file
        let text:string = reader.result.toString();
        let lines = text.split('\n');
        this.processNextLine(lines, lines.length);
        for (let i=0; i < lines.length; i++){
          this.progress = i/lines.length * 100;
          //console.error(lines[i]);
        }
        //Split and Post Lines with progress bar

      }
      reader.readAsText(this.file);
    }

  }

  processNextLine(lines:Array<string>, total:number){
    if (lines.length > 0){
      let line:string = lines[0].trim();
      lines.shift();
      //this.logger.debug('PROCESSING ' + line);
      if (line.length > 0){
        let values:Array<string> = line.split(/,(?=(?:(?:[^"]*"){2})*[^"]*$)/);
        //console.error(JSON.stringify(values));

        let ded:DailyEnergyData = new DailyEnergyData();
        ded.mtuId=FormatHelper.removeCSVQuotes(values[0]);
        ded.energyDate=FormatHelper.convertToEnergyDate(FormatHelper.removeCSVQuotes(values[1]));
        ded.energyValue=parseFloat(FormatHelper.removeCSVQuotes(values[2]).toLowerCase()) * 1000.0;

        if (ded.energyDate == null){
          console.warn('[processNextLine] Skipping ' + line);
          this.progress = Math.ceil(((total-lines.length)/total) * 100);
          this.processNextLine(lines, total);
        } else {
          console.debug('[processNextLine] Importing ' + line);
          this.dailyEnergyService.update(ded)
            .then((ded: DailyEnergyData) => {
              this.progress = Math.ceil(((total - lines.length) / total) * 100);
              this.processNextLine(lines, total);
            })
            .catch((err) => {
              this.progress = Math.ceil(((total - lines.length) / total) * 100);
              this.processNextLine(lines, total);
            })
        }
      } else {
        this.progress = Math.ceil(((total-lines.length)/total) * 100);
        this.processNextLine(lines, total);
      }




    } else {
      this.dialogRef.close(true);
    }

  }

  onSelectedFileChanged(event: File) {
    this.hasFile = event != null;
    this.file = event;
  }

  ngAfterContentInit(): void {
      this.progress = 0;
      this.isUploading = false;
  }
}
