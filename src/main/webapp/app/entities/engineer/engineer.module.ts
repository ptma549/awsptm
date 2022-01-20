import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EngineerComponent } from './list/engineer.component';
import { EngineerDetailComponent } from './detail/engineer-detail.component';
import { EngineerUpdateComponent } from './update/engineer-update.component';
import { EngineerDeleteDialogComponent } from './delete/engineer-delete-dialog.component';
import { EngineerRoutingModule } from './route/engineer-routing.module';

@NgModule({
  imports: [SharedModule, EngineerRoutingModule],
  declarations: [EngineerComponent, EngineerDetailComponent, EngineerUpdateComponent, EngineerDeleteDialogComponent],
  entryComponents: [EngineerDeleteDialogComponent],
})
export class EngineerModule {}
