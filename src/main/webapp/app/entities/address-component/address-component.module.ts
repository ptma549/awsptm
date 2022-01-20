import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AddressComponentComponent } from './list/address-component.component';
import { AddressComponentDetailComponent } from './detail/address-component-detail.component';
import { AddressComponentUpdateComponent } from './update/address-component-update.component';
import { AddressComponentDeleteDialogComponent } from './delete/address-component-delete-dialog.component';
import { AddressComponentRoutingModule } from './route/address-component-routing.module';

@NgModule({
  imports: [SharedModule, AddressComponentRoutingModule],
  declarations: [
    AddressComponentComponent,
    AddressComponentDetailComponent,
    AddressComponentUpdateComponent,
    AddressComponentDeleteDialogComponent,
  ],
  entryComponents: [AddressComponentDeleteDialogComponent],
})
export class AddressComponentModule {}
