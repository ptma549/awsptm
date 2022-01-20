import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICertificate, Certificate } from '../certificate.model';
import { CertificateService } from '../service/certificate.service';

@Injectable({ providedIn: 'root' })
export class CertificateRoutingResolveService implements Resolve<ICertificate> {
  constructor(protected service: CertificateService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICertificate> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((certificate: HttpResponse<Certificate>) => {
          if (certificate.body) {
            return of(certificate.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Certificate());
  }
}
