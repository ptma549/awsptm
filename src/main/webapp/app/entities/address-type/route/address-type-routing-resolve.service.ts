import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAddressType, AddressType } from '../address-type.model';
import { AddressTypeService } from '../service/address-type.service';

@Injectable({ providedIn: 'root' })
export class AddressTypeRoutingResolveService implements Resolve<IAddressType> {
  constructor(protected service: AddressTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAddressType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((addressType: HttpResponse<AddressType>) => {
          if (addressType.body) {
            return of(addressType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AddressType());
  }
}
