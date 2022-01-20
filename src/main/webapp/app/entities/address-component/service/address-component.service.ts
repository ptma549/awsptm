import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAddressComponent, getAddressComponentIdentifier } from '../address-component.model';

export type EntityResponseType = HttpResponse<IAddressComponent>;
export type EntityArrayResponseType = HttpResponse<IAddressComponent[]>;

@Injectable({ providedIn: 'root' })
export class AddressComponentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/address-components');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(addressComponent: IAddressComponent): Observable<EntityResponseType> {
    return this.http.post<IAddressComponent>(this.resourceUrl, addressComponent, { observe: 'response' });
  }

  update(addressComponent: IAddressComponent): Observable<EntityResponseType> {
    return this.http.put<IAddressComponent>(
      `${this.resourceUrl}/${getAddressComponentIdentifier(addressComponent) as number}`,
      addressComponent,
      { observe: 'response' }
    );
  }

  partialUpdate(addressComponent: IAddressComponent): Observable<EntityResponseType> {
    return this.http.patch<IAddressComponent>(
      `${this.resourceUrl}/${getAddressComponentIdentifier(addressComponent) as number}`,
      addressComponent,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAddressComponent>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddressComponent[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAddressComponentToCollectionIfMissing(
    addressComponentCollection: IAddressComponent[],
    ...addressComponentsToCheck: (IAddressComponent | null | undefined)[]
  ): IAddressComponent[] {
    const addressComponents: IAddressComponent[] = addressComponentsToCheck.filter(isPresent);
    if (addressComponents.length > 0) {
      const addressComponentCollectionIdentifiers = addressComponentCollection.map(
        addressComponentItem => getAddressComponentIdentifier(addressComponentItem)!
      );
      const addressComponentsToAdd = addressComponents.filter(addressComponentItem => {
        const addressComponentIdentifier = getAddressComponentIdentifier(addressComponentItem);
        if (addressComponentIdentifier == null || addressComponentCollectionIdentifiers.includes(addressComponentIdentifier)) {
          return false;
        }
        addressComponentCollectionIdentifiers.push(addressComponentIdentifier);
        return true;
      });
      return [...addressComponentsToAdd, ...addressComponentCollection];
    }
    return addressComponentCollection;
  }
}
