import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEngineer, getEngineerIdentifier } from '../engineer.model';

export type EntityResponseType = HttpResponse<IEngineer>;
export type EntityArrayResponseType = HttpResponse<IEngineer[]>;

@Injectable({ providedIn: 'root' })
export class EngineerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/engineers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(engineer: IEngineer): Observable<EntityResponseType> {
    return this.http.post<IEngineer>(this.resourceUrl, engineer, { observe: 'response' });
  }

  update(engineer: IEngineer): Observable<EntityResponseType> {
    return this.http.put<IEngineer>(`${this.resourceUrl}/${getEngineerIdentifier(engineer) as number}`, engineer, { observe: 'response' });
  }

  partialUpdate(engineer: IEngineer): Observable<EntityResponseType> {
    return this.http.patch<IEngineer>(`${this.resourceUrl}/${getEngineerIdentifier(engineer) as number}`, engineer, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEngineer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEngineer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEngineerToCollectionIfMissing(engineerCollection: IEngineer[], ...engineersToCheck: (IEngineer | null | undefined)[]): IEngineer[] {
    const engineers: IEngineer[] = engineersToCheck.filter(isPresent);
    if (engineers.length > 0) {
      const engineerCollectionIdentifiers = engineerCollection.map(engineerItem => getEngineerIdentifier(engineerItem)!);
      const engineersToAdd = engineers.filter(engineerItem => {
        const engineerIdentifier = getEngineerIdentifier(engineerItem);
        if (engineerIdentifier == null || engineerCollectionIdentifiers.includes(engineerIdentifier)) {
          return false;
        }
        engineerCollectionIdentifiers.push(engineerIdentifier);
        return true;
      });
      return [...engineersToAdd, ...engineerCollection];
    }
    return engineerCollection;
  }
}
