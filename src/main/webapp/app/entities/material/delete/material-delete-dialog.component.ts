import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMaterial } from '../material.model';
import { MaterialService } from '../service/material.service';

@Component({
  templateUrl: './material-delete-dialog.component.html',
})
export class MaterialDeleteDialogComponent {
  material?: IMaterial;

  constructor(protected materialService: MaterialService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.materialService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
