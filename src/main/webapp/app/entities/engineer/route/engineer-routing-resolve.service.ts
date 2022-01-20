import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEngineer, Engineer } from '../engineer.model';
import { EngineerService } from '../service/engineer.service';

@Injectable({ providedIn: 'root' })
export class EngineerRoutingResolveService implements Resolve<IEngineer> {
  constructor(protected service: EngineerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEngineer> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((engineer: HttpResponse<Engineer>) => {
          if (engineer.body) {
            return of(engineer.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Engineer());
  }
}
