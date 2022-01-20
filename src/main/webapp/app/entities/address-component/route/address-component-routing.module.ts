import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AddressComponentComponent } from '../list/address-component.component';
import { AddressComponentDetailComponent } from '../detail/address-component-detail.component';
import { AddressComponentUpdateComponent } from '../update/address-component-update.component';
import { AddressComponentRoutingResolveService } from './address-component-routing-resolve.service';

const addressComponentRoute: Routes = [
  {
    path: '',
    component: AddressComponentComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AddressComponentDetailComponent,
    resolve: {
      addressComponent: AddressComponentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AddressComponentUpdateComponent,
    resolve: {
      addressComponent: AddressComponentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AddressComponentUpdateComponent,
    resolve: {
      addressComponent: AddressComponentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(addressComponentRoute)],
  exports: [RouterModule],
})
export class AddressComponentRoutingModule {}
