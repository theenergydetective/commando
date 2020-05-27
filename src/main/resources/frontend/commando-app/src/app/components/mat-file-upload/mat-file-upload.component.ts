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

import {Component, ElementRef, EventEmitter, HostListener, Input, Output} from '@angular/core';
import {NG_VALUE_ACCESSOR} from "@angular/forms";

@Component({
  selector: 'mat-file-upload',
  templateUrl: './mat-file-upload.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: MatFileUploadComponent,
      multi: true
    }
  ],
  styleUrls: ['./mat-file-upload.component.scss']
})
export class MatFileUploadComponent{
  @Input() progress;
  onChange: Function;
  private file: File | null = null;

  @Output() selected: EventEmitter<File> = new EventEmitter();

  @HostListener('change', ['$event.target.files']) emitFiles( event: FileList ) {
    const file = event && event.item(0);
    this.file = file;
    this.selected.emit(file);
  }

  constructor( private host: ElementRef<HTMLInputElement> ) {
  }

}
