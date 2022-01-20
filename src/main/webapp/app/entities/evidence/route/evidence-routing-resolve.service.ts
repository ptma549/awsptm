import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEvidence, Evidence } from '../evidence.model';
import { EvidenceService } from '../service/evidence.service';

@Injectable({ providedIn: 'root' })
export class EvidenceRoutingResolveService implements Resolve<IEvidence> {
  constructor(protected service: EvidenceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEvidence> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((evidence: HttpResponse<Evidence>) => {
          if (evidence.body) {
            return of(evidence.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Evidence());
  }
}
