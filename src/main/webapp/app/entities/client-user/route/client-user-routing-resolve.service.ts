import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClientUser, ClientUser } from '../client-user.model';
import { ClientUserService } from '../service/client-user.service';

@Injectable({ providedIn: 'root' })
export class ClientUserRoutingResolveService implements Resolve<IClientUser> {
  constructor(protected service: ClientUserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IClientUser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((clientUser: HttpResponse<ClientUser>) => {
          if (clientUser.body) {
            return of(clientUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ClientUser());
  }
}
