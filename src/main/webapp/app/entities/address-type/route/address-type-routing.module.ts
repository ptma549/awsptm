import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AddressTypeComponent } from '../list/address-type.component';
import { AddressTypeDetailComponent } from '../detail/address-type-detail.component';
import { AddressTypeUpdateComponent } from '../update/address-type-update.component';
import { AddressTypeRoutingResolveService } from './address-type-routing-resolve.service';

const addressTypeRoute: Routes = [
  {
    path: '',
    component: AddressTypeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AddressTypeDetailComponent,
    resolve: {
      addressType: AddressTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AddressTypeUpdateComponent,
    resolve: {
      addressType: AddressTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AddressTypeUpdateComponent,
    resolve: {
      addressType: AddressTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(addressTypeRoute)],
  exports: [RouterModule],
})
export class AddressTypeRoutingModule {}
