import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVisit } from '../visit.model';
import { VisitService } from '../service/visit.service';

@Component({
  templateUrl: './visit-delete-dialog.component.html',
})
export class VisitDeleteDialogComponent {
  visit?: IVisit;

  constructor(protected visitService: VisitService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.visitService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
