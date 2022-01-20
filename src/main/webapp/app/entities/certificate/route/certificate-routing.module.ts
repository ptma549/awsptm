import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CertificateComponent } from '../list/certificate.component';
import { CertificateDetailComponent } from '../detail/certificate-detail.component';
import { CertificateUpdateComponent } from '../update/certificate-update.component';
import { CertificateRoutingResolveService } from './certificate-routing-resolve.service';

const certificateRoute: Routes = [
  {
    path: '',
    component: CertificateComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CertificateDetailComponent,
    resolve: {
      certificate: CertificateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CertificateUpdateComponent,
    resolve: {
      certificate: CertificateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CertificateUpdateComponent,
    resolve: {
      certificate: CertificateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(certificateRoute)],
  exports: [RouterModule],
})
export class CertificateRoutingModule {}
