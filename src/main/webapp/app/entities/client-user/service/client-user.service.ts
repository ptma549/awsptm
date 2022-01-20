import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClientUser, getClientUserIdentifier } from '../client-user.model';

export type EntityResponseType = HttpResponse<IClientUser>;
export type EntityArrayResponseType = HttpResponse<IClientUser[]>;

@Injectable({ providedIn: 'root' })
export class ClientUserService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/client-users');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(clientUser: IClientUser): Observable<EntityResponseType> {
    return this.http.post<IClientUser>(this.resourceUrl, clientUser, { observe: 'response' });
  }

  update(clientUser: IClientUser): Observable<EntityResponseType> {
    return this.http.put<IClientUser>(`${this.resourceUrl}/${getClientUserIdentifier(clientUser) as number}`, clientUser, {
      observe: 'response',
    });
  }

  partialUpdate(clientUser: IClientUser): Observable<EntityResponseType> {
    return this.http.patch<IClientUser>(`${this.resourceUrl}/${getClientUserIdentifier(clientUser) as number}`, clientUser, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClientUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClientUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addClientUserToCollectionIfMissing(
    clientUserCollection: IClientUser[],
    ...clientUsersToCheck: (IClientUser | null | undefined)[]
  ): IClientUser[] {
    const clientUsers: IClientUser[] = clientUsersToCheck.filter(isPresent);
    if (clientUsers.length > 0) {
      const clientUserCollectionIdentifiers = clientUserCollection.map(clientUserItem => getClientUserIdentifier(clientUserItem)!);
      const clientUsersToAdd = clientUsers.filter(clientUserItem => {
        const clientUserIdentifier = getClientUserIdentifier(clientUserItem);
        if (clientUserIdentifier == null || clientUserCollectionIdentifiers.includes(clientUserIdentifier)) {
          return false;
        }
        clientUserCollectionIdentifiers.push(clientUserIdentifier);
        return true;
      });
      return [...clientUsersToAdd, ...clientUserCollection];
    }
    return clientUserCollection;
  }
}
