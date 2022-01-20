import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClientOrganisation, getClientOrganisationIdentifier } from '../client-organisation.model';

export type EntityResponseType = HttpResponse<IClientOrganisation>;
export type EntityArrayResponseType = HttpResponse<IClientOrganisation[]>;

@Injectable({ providedIn: 'root' })
export class ClientOrganisationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/client-organisations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(clientOrganisation: IClientOrganisation): Observable<EntityResponseType> {
    return this.http.post<IClientOrganisation>(this.resourceUrl, clientOrganisation, { observe: 'response' });
  }

  update(clientOrganisation: IClientOrganisation): Observable<EntityResponseType> {
    return this.http.put<IClientOrganisation>(
      `${this.resourceUrl}/${getClientOrganisationIdentifier(clientOrganisation) as number}`,
      clientOrganisation,
      { observe: 'response' }
    );
  }

  partialUpdate(clientOrganisation: IClientOrganisation): Observable<EntityResponseType> {
    return this.http.patch<IClientOrganisation>(
      `${this.resourceUrl}/${getClientOrganisationIdentifier(clientOrganisation) as number}`,
      clientOrganisation,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClientOrganisation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClientOrganisation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addClientOrganisationToCollectionIfMissing(
    clientOrganisationCollection: IClientOrganisation[],
    ...clientOrganisationsToCheck: (IClientOrganisation | null | undefined)[]
  ): IClientOrganisation[] {
    const clientOrganisations: IClientOrganisation[] = clientOrganisationsToCheck.filter(isPresent);
    if (clientOrganisations.length > 0) {
      const clientOrganisationCollectionIdentifiers = clientOrganisationCollection.map(
        clientOrganisationItem => getClientOrganisationIdentifier(clientOrganisationItem)!
      );
      const clientOrganisationsToAdd = clientOrganisations.filter(clientOrganisationItem => {
        const clientOrganisationIdentifier = getClientOrganisationIdentifier(clientOrganisationItem);
        if (clientOrganisationIdentifier == null || clientOrganisationCollectionIdentifiers.includes(clientOrganisationIdentifier)) {
          return false;
        }
        clientOrganisationCollectionIdentifiers.push(clientOrganisationIdentifier);
        return true;
      });
      return [...clientOrganisationsToAdd, ...clientOrganisationCollection];
    }
    return clientOrganisationCollection;
  }
}
