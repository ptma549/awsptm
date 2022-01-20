import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAddressType } from '../address-type.model';
import { AddressTypeService } from '../service/address-type.service';

@Component({
  templateUrl: './address-type-delete-dialog.component.html',
})
export class AddressTypeDeleteDialogComponent {
  addressType?: IAddressType;

  constructor(protected addressTypeService: AddressTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.addressTypeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
