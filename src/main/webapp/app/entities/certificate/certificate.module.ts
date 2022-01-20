import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CertificateComponent } from './list/certificate.component';
import { CertificateDetailComponent } from './detail/certificate-detail.component';
import { CertificateUpdateComponent } from './update/certificate-update.component';
import { CertificateDeleteDialogComponent } from './delete/certificate-delete-dialog.component';
import { CertificateRoutingModule } from './route/certificate-routing.module';

@NgModule({
  imports: [SharedModule, CertificateRoutingModule],
  declarations: [CertificateComponent, CertificateDetailComponent, CertificateUpdateComponent, CertificateDeleteDialogComponent],
  entryComponents: [CertificateDeleteDialogComponent],
})
export class CertificateModule {}
