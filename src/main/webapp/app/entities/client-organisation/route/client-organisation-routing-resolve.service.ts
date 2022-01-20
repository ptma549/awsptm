import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClientOrganisation, ClientOrganisation } from '../client-organisation.model';
import { ClientOrganisationService } from '../service/client-organisation.service';

@Injectable({ providedIn: 'root' })
export class ClientOrganisationRoutingResolveService implements Resolve<IClientOrganisation> {
  constructor(protected service: ClientOrganisationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IClientOrganisation> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((clientOrganisation: HttpResponse<ClientOrganisation>) => {
          if (clientOrganisation.body) {
            return of(clientOrganisation.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ClientOrganisation());
  }
}
