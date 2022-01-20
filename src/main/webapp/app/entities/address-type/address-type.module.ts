import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AddressTypeComponent } from './list/address-type.component';
import { AddressTypeDetailComponent } from './detail/address-type-detail.component';
import { AddressTypeUpdateComponent } from './update/address-type-update.component';
import { AddressTypeDeleteDialogComponent } from './delete/address-type-delete-dialog.component';
import { AddressTypeRoutingModule } from './route/address-type-routing.module';

@NgModule({
  imports: [SharedModule, AddressTypeRoutingModule],
  declarations: [AddressTypeComponent, AddressTypeDetailComponent, AddressTypeUpdateComponent, AddressTypeDeleteDialogComponent],
  entryComponents: [AddressTypeDeleteDialogComponent],
})
export class AddressTypeModule {}
