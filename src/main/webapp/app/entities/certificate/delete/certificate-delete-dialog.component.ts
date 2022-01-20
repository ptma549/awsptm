import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICertificate } from '../certificate.model';
import { CertificateService } from '../service/certificate.service';

@Component({
  templateUrl: './certificate-delete-dialog.component.html',
})
export class CertificateDeleteDialogComponent {
  certificate?: ICertificate;

  constructor(protected certificateService: CertificateService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.certificateService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
