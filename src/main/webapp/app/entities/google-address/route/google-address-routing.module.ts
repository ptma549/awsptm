import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GoogleAddressComponent } from '../list/google-address.component';
import { GoogleAddressDetailComponent } from '../detail/google-address-detail.component';
import { GoogleAddressUpdateComponent } from '../update/google-address-update.component';
import { GoogleAddressRoutingResolveService } from './google-address-routing-resolve.service';

const googleAddressRoute: Routes = [
  {
    path: '',
    component: GoogleAddressComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GoogleAddressDetailComponent,
    resolve: {
      googleAddress: GoogleAddressRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GoogleAddressUpdateComponent,
    resolve: {
      googleAddress: GoogleAddressRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GoogleAddressUpdateComponent,
    resolve: {
      googleAddress: GoogleAddressRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(googleAddressRoute)],
  exports: [RouterModule],
})
export class GoogleAddressRoutingModule {}
