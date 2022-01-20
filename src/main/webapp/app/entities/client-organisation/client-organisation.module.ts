import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ClientOrganisationComponent } from './list/client-organisation.component';
import { ClientOrganisationDetailComponent } from './detail/client-organisation-detail.component';
import { ClientOrganisationUpdateComponent } from './update/client-organisation-update.component';
import { ClientOrganisationDeleteDialogComponent } from './delete/client-organisation-delete-dialog.component';
import { ClientOrganisationRoutingModule } from './route/client-organisation-routing.module';

@NgModule({
  imports: [SharedModule, ClientOrganisationRoutingModule],
  declarations: [
    ClientOrganisationComponent,
    ClientOrganisationDetailComponent,
    ClientOrganisationUpdateComponent,
    ClientOrganisationDeleteDialogComponent,
  ],
  entryComponents: [ClientOrganisationDeleteDialogComponent],
})
export class ClientOrganisationModule {}
