import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGoogleAddress, GoogleAddress } from '../google-address.model';
import { GoogleAddressService } from '../service/google-address.service';

@Injectable({ providedIn: 'root' })
export class GoogleAddressRoutingResolveService implements Resolve<IGoogleAddress> {
  constructor(protected service: GoogleAddressService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGoogleAddress> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((googleAddress: HttpResponse<GoogleAddress>) => {
          if (googleAddress.body) {
            return of(googleAddress.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GoogleAddress());
  }
}
