import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ClientUserComponent } from '../list/client-user.component';
import { ClientUserDetailComponent } from '../detail/client-user-detail.component';
import { ClientUserUpdateComponent } from '../update/client-user-update.component';
import { ClientUserRoutingResolveService } from './client-user-routing-resolve.service';

const clientUserRoute: Routes = [
  {
    path: '',
    component: ClientUserComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClientUserDetailComponent,
    resolve: {
      clientUser: ClientUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClientUserUpdateComponent,
    resolve: {
      clientUser: ClientUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClientUserUpdateComponent,
    resolve: {
      clientUser: ClientUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(clientUserRoute)],
  exports: [RouterModule],
})
export class ClientUserRoutingModule {}
