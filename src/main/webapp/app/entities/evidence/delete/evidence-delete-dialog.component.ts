import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEvidence } from '../evidence.model';
import { EvidenceService } from '../service/evidence.service';

@Component({
  templateUrl: './evidence-delete-dialog.component.html',
})
export class EvidenceDeleteDialogComponent {
  evidence?: IEvidence;

  constructor(protected evidenceService: EvidenceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.evidenceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
