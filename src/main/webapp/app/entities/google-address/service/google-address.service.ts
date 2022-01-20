import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGoogleAddress, getGoogleAddressIdentifier } from '../google-address.model';

export type EntityResponseType = HttpResponse<IGoogleAddress>;
export type EntityArrayResponseType = HttpResponse<IGoogleAddress[]>;

@Injectable({ providedIn: 'root' })
export class GoogleAddressService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/google-addresses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(googleAddress: IGoogleAddress): Observable<EntityResponseType> {
    return this.http.post<IGoogleAddress>(this.resourceUrl, googleAddress, { observe: 'response' });
  }

  update(googleAddress: IGoogleAddress): Observable<EntityResponseType> {
    return this.http.put<IGoogleAddress>(`${this.resourceUrl}/${getGoogleAddressIdentifier(googleAddress) as number}`, googleAddress, {
      observe: 'response',
    });
  }

  partialUpdate(googleAddress: IGoogleAddress): Observable<EntityResponseType> {
    return this.http.patch<IGoogleAddress>(`${this.resourceUrl}/${getGoogleAddressIdentifier(googleAddress) as number}`, googleAddress, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGoogleAddress>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGoogleAddress[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGoogleAddressToCollectionIfMissing(
    googleAddressCollection: IGoogleAddress[],
    ...googleAddressesToCheck: (IGoogleAddress | null | undefined)[]
  ): IGoogleAddress[] {
    const googleAddresses: IGoogleAddress[] = googleAddressesToCheck.filter(isPresent);
    if (googleAddresses.length > 0) {
      const googleAddressCollectionIdentifiers = googleAddressCollection.map(
        googleAddressItem => getGoogleAddressIdentifier(googleAddressItem)!
      );
      const googleAddressesToAdd = googleAddresses.filter(googleAddressItem => {
        const googleAddressIdentifier = getGoogleAddressIdentifier(googleAddressItem);
        if (googleAddressIdentifier == null || googleAddressCollectionIdentifiers.includes(googleAddressIdentifier)) {
          return false;
        }
        googleAddressCollectionIdentifiers.push(googleAddressIdentifier);
        return true;
      });
      return [...googleAddressesToAdd, ...googleAddressCollection];
    }
    return googleAddressCollection;
  }
}
