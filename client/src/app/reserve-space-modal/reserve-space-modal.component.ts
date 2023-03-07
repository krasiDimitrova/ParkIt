import { Component } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-reserve-space-modal',
  templateUrl: './reserve-space-modal.component.html',
  styleUrls: ['./reserve-space-modal.component.css']
})
export class ReserveSpaceModalComponent {
  constructor(private dialog: MatDialog, private dialogRef: MatDialogRef<ReserveSpaceModalComponent>) {
  }

  save() {
    this.dialogRef.close(true);
  }

  close() {
    this.dialogRef.close(false);
  }

}
