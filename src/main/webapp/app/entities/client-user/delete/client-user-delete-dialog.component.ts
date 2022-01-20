import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IClientUser } from '../client-user.model';
import { ClientUserService } from '../service/client-user.service';

@Component({
  templateUrl: './client-user-delete-dialog.component.html',
})
export class ClientUserDeleteDialogComponent {
  clientUser?: IClientUser;

  constructor(protected clientUserService: ClientUserService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clientUserService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
