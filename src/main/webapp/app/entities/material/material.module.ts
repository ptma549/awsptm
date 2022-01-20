import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MaterialComponent } from './list/material.component';
import { MaterialDetailComponent } from './detail/material-detail.component';
import { MaterialUpdateComponent } from './update/material-update.component';
import { MaterialDeleteDialogComponent } from './delete/material-delete-dialog.component';
import { MaterialRoutingModule } from './route/material-routing.module';

@NgModule({
  imports: [SharedModule, MaterialRoutingModule],
  declarations: [MaterialComponent, MaterialDetailComponent, MaterialUpdateComponent, MaterialDeleteDialogComponent],
  entryComponents: [MaterialDeleteDialogComponent],
})
export class MaterialModule {}
