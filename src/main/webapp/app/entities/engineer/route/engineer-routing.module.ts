import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EngineerComponent } from '../list/engineer.component';
import { EngineerDetailComponent } from '../detail/engineer-detail.component';
import { EngineerUpdateComponent } from '../update/engineer-update.component';
import { EngineerRoutingResolveService } from './engineer-routing-resolve.service';

const engineerRoute: Routes = [
  {
    path: '',
    component: EngineerComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EngineerDetailComponent,
    resolve: {
      engineer: EngineerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EngineerUpdateComponent,
    resolve: {
      engineer: EngineerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EngineerUpdateComponent,
    resolve: {
      engineer: EngineerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(engineerRoute)],
  exports: [RouterModule],
})
export class EngineerRoutingModule {}
