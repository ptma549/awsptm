import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GoogleAddressComponent } from './list/google-address.component';
import { GoogleAddressDetailComponent } from './detail/google-address-detail.component';
import { GoogleAddressUpdateComponent } from './update/google-address-update.component';
import { GoogleAddressDeleteDialogComponent } from './delete/google-address-delete-dialog.component';
import { GoogleAddressRoutingModule } from './route/google-address-routing.module';

@NgModule({
  imports: [SharedModule, GoogleAddressRoutingModule],
  declarations: [GoogleAddressComponent, GoogleAddressDetailComponent, GoogleAddressUpdateComponent, GoogleAddressDeleteDialogComponent],
  entryComponents: [GoogleAddressDeleteDialogComponent],
})
export class GoogleAddressModule {}
