import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGoogleAddress } from '../google-address.model';
import { GoogleAddressService } from '../service/google-address.service';

@Component({
  templateUrl: './google-address-delete-dialog.component.html',
})
export class GoogleAddressDeleteDialogComponent {
  googleAddress?: IGoogleAddress;

  constructor(protected googleAddressService: GoogleAddressService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.googleAddressService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
