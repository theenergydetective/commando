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


@Component({
  selector: 'import-devices-dialog',
  templateUrl: './import-devices-dialog.component.html',
  styleUrls: ['./import-devices-dialog.component.scss']
})
export class ImportDevicesDialogComponent implements AfterContentInit{
  hasFile: boolean = false;
  private file:File = null;
  isUploading: boolean = false;
  progress: number = 0;

  constructor(
    private mtuService:MtuService,
    public dialogRef: MatDialogRef<ImportDevicesDialogComponent>,
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
        lines.shift(); //Pop the first line
        this.processNextLine(lines, lines.length);
        for (let i=0; i < lines.length; i++){
          this.progress = i/lines.length * 100;
          console.error(lines[i]);
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
        console.error(JSON.stringify(values));

        let mtu:MeasuringTransmittingUnit = new MeasuringTransmittingUnit();
        mtu.id=FormatHelper.removeCSVQuotes(values[0]);

        //If it contains wrapping quotes, remove it.
        mtu.name=FormatHelper.removeCSVQuotes(values[1]).trim();


        mtu.rate=parseFloat(FormatHelper.removeCSVQuotes(values[2]));
        mtu.enabled=FormatHelper.removeCSVQuotes(values[3]).toLowerCase() != 'false';
        this.mtuService.updateSettings(mtu)
          .then((mtu:MeasuringTransmittingUnit)=>{
            this.progress = Math.ceil(((total-lines.length)/total) * 100);
            this.processNextLine(lines, total);
          })
          .catch((err)=>{
            this.progress = Math.ceil(((total-lines.length)/total) * 100);
            this.processNextLine(lines, total);
          })
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
