import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAddressComponent } from '../address-component.model';
import { AddressComponentService } from '../service/address-component.service';

@Component({
  templateUrl: './address-component-delete-dialog.component.html',
})
export class AddressComponentDeleteDialogComponent {
  addressComponent?: IAddressComponent;

  constructor(protected addressComponentService: AddressComponentService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.addressComponentService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
