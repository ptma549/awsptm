import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICertificate, getCertificateIdentifier } from '../certificate.model';

export type EntityResponseType = HttpResponse<ICertificate>;
export type EntityArrayResponseType = HttpResponse<ICertificate[]>;

@Injectable({ providedIn: 'root' })
export class CertificateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/certificates');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(certificate: ICertificate): Observable<EntityResponseType> {
    return this.http.post<ICertificate>(this.resourceUrl, certificate, { observe: 'response' });
  }

  update(certificate: ICertificate): Observable<EntityResponseType> {
    return this.http.put<ICertificate>(`${this.resourceUrl}/${getCertificateIdentifier(certificate) as number}`, certificate, {
      observe: 'response',
    });
  }

  partialUpdate(certificate: ICertificate): Observable<EntityResponseType> {
    return this.http.patch<ICertificate>(`${this.resourceUrl}/${getCertificateIdentifier(certificate) as number}`, certificate, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICertificate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICertificate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCertificateToCollectionIfMissing(
    certificateCollection: ICertificate[],
    ...certificatesToCheck: (ICertificate | null | undefined)[]
  ): ICertificate[] {
    const certificates: ICertificate[] = certificatesToCheck.filter(isPresent);
    if (certificates.length > 0) {
      const certificateCollectionIdentifiers = certificateCollection.map(certificateItem => getCertificateIdentifier(certificateItem)!);
      const certificatesToAdd = certificates.filter(certificateItem => {
        const certificateIdentifier = getCertificateIdentifier(certificateItem);
        if (certificateIdentifier == null || certificateCollectionIdentifiers.includes(certificateIdentifier)) {
          return false;
        }
        certificateCollectionIdentifiers.push(certificateIdentifier);
        return true;
      });
      return [...certificatesToAdd, ...certificateCollection];
    }
    return certificateCollection;
  }
}
