import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MaterialComponent } from '../list/material.component';
import { MaterialDetailComponent } from '../detail/material-detail.component';
import { MaterialUpdateComponent } from '../update/material-update.component';
import { MaterialRoutingResolveService } from './material-routing-resolve.service';

const materialRoute: Routes = [
  {
    path: '',
    component: MaterialComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MaterialDetailComponent,
    resolve: {
      material: MaterialRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MaterialUpdateComponent,
    resolve: {
      material: MaterialRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MaterialUpdateComponent,
    resolve: {
      material: MaterialRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(materialRoute)],
  exports: [RouterModule],
})
export class MaterialRoutingModule {}
