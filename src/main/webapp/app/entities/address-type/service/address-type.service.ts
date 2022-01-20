import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAddressType, getAddressTypeIdentifier } from '../address-type.model';

export type EntityResponseType = HttpResponse<IAddressType>;
export type EntityArrayResponseType = HttpResponse<IAddressType[]>;

@Injectable({ providedIn: 'root' })
export class AddressTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/address-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(addressType: IAddressType): Observable<EntityResponseType> {
    return this.http.post<IAddressType>(this.resourceUrl, addressType, { observe: 'response' });
  }

  update(addressType: IAddressType): Observable<EntityResponseType> {
    return this.http.put<IAddressType>(`${this.resourceUrl}/${getAddressTypeIdentifier(addressType) as number}`, addressType, {
      observe: 'response',
    });
  }

  partialUpdate(addressType: IAddressType): Observable<EntityResponseType> {
    return this.http.patch<IAddressType>(`${this.resourceUrl}/${getAddressTypeIdentifier(addressType) as number}`, addressType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAddressType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddressType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAddressTypeToCollectionIfMissing(
    addressTypeCollection: IAddressType[],
    ...addressTypesToCheck: (IAddressType | null | undefined)[]
  ): IAddressType[] {
    const addressTypes: IAddressType[] = addressTypesToCheck.filter(isPresent);
    if (addressTypes.length > 0) {
      const addressTypeCollectionIdentifiers = addressTypeCollection.map(addressTypeItem => getAddressTypeIdentifier(addressTypeItem)!);
      const addressTypesToAdd = addressTypes.filter(addressTypeItem => {
        const addressTypeIdentifier = getAddressTypeIdentifier(addressTypeItem);
        if (addressTypeIdentifier == null || addressTypeCollectionIdentifiers.includes(addressTypeIdentifier)) {
          return false;
        }
        addressTypeCollectionIdentifiers.push(addressTypeIdentifier);
        return true;
      });
      return [...addressTypesToAdd, ...addressTypeCollection];
    }
    return addressTypeCollection;
  }
}
