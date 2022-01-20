import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAddressComponent, AddressComponent } from '../address-component.model';
import { AddressComponentService } from '../service/address-component.service';

@Injectable({ providedIn: 'root' })
export class AddressComponentRoutingResolveService implements Resolve<IAddressComponent> {
  constructor(protected service: AddressComponentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAddressComponent> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((addressComponent: HttpResponse<AddressComponent>) => {
          if (addressComponent.body) {
            return of(addressComponent.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AddressComponent());
  }
}
