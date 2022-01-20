import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ClientOrganisationComponent } from '../list/client-organisation.component';
import { ClientOrganisationDetailComponent } from '../detail/client-organisation-detail.component';
import { ClientOrganisationUpdateComponent } from '../update/client-organisation-update.component';
import { ClientOrganisationRoutingResolveService } from './client-organisation-routing-resolve.service';

const clientOrganisationRoute: Routes = [
  {
    path: '',
    component: ClientOrganisationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClientOrganisationDetailComponent,
    resolve: {
      clientOrganisation: ClientOrganisationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClientOrganisationUpdateComponent,
    resolve: {
      clientOrganisation: ClientOrganisationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClientOrganisationUpdateComponent,
    resolve: {
      clientOrganisation: ClientOrganisationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(clientOrganisationRoute)],
  exports: [RouterModule],
})
export class ClientOrganisationRoutingModule {}
