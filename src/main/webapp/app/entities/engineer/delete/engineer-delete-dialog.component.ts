import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEngineer } from '../engineer.model';
import { EngineerService } from '../service/engineer.service';

@Component({
  templateUrl: './engineer-delete-dialog.component.html',
})
export class EngineerDeleteDialogComponent {
  engineer?: IEngineer;

  constructor(protected engineerService: EngineerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.engineerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
