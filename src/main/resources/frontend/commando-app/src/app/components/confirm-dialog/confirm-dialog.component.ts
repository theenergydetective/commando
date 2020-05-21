import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";


@Component({
  selector: 'app-error-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent implements OnInit {

  public titleString: string;
  public bodyString:string[];

  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }

  ngOnInit() {
    this.titleString = this.data.title;
    this.bodyString = this.data.body;
  }

  onCloseClick(): void {
    this.dialogRef.close(false);
  }

  onClickConfirm() {
    this.dialogRef.close(true);
  }
}
