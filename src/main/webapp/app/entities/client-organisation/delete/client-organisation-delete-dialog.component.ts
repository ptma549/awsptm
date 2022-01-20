import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IClientOrganisation } from '../client-organisation.model';
import { ClientOrganisationService } from '../service/client-organisation.service';

@Component({
  templateUrl: './client-organisation-delete-dialog.component.html',
})
export class ClientOrganisationDeleteDialogComponent {
  clientOrganisation?: IClientOrganisation;

  constructor(protected clientOrganisationService: ClientOrganisationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clientOrganisationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
