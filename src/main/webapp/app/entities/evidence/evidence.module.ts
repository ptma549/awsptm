import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EvidenceComponent } from './list/evidence.component';
import { EvidenceDetailComponent } from './detail/evidence-detail.component';
import { EvidenceUpdateComponent } from './update/evidence-update.component';
import { EvidenceDeleteDialogComponent } from './delete/evidence-delete-dialog.component';
import { EvidenceRoutingModule } from './route/evidence-routing.module';

@NgModule({
  imports: [SharedModule, EvidenceRoutingModule],
  declarations: [EvidenceComponent, EvidenceDetailComponent, EvidenceUpdateComponent, EvidenceDeleteDialogComponent],
  entryComponents: [EvidenceDeleteDialogComponent],
})
export class EvidenceModule {}
