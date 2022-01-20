import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EvidenceComponent } from '../list/evidence.component';
import { EvidenceDetailComponent } from '../detail/evidence-detail.component';
import { EvidenceUpdateComponent } from '../update/evidence-update.component';
import { EvidenceRoutingResolveService } from './evidence-routing-resolve.service';

const evidenceRoute: Routes = [
  {
    path: '',
    component: EvidenceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EvidenceDetailComponent,
    resolve: {
      evidence: EvidenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EvidenceUpdateComponent,
    resolve: {
      evidence: EvidenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EvidenceUpdateComponent,
    resolve: {
      evidence: EvidenceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(evidenceRoute)],
  exports: [RouterModule],
})
export class EvidenceRoutingModule {}
