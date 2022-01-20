import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ClientUserComponent } from './list/client-user.component';
import { ClientUserDetailComponent } from './detail/client-user-detail.component';
import { ClientUserUpdateComponent } from './update/client-user-update.component';
import { ClientUserDeleteDialogComponent } from './delete/client-user-delete-dialog.component';
import { ClientUserRoutingModule } from './route/client-user-routing.module';

@NgModule({
  imports: [SharedModule, ClientUserRoutingModule],
  declarations: [ClientUserComponent, ClientUserDetailComponent, ClientUserUpdateComponent, ClientUserDeleteDialogComponent],
  entryComponents: [ClientUserDeleteDialogComponent],
})
export class ClientUserModule {}
