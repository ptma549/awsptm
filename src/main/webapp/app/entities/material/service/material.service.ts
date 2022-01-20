import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMaterial, getMaterialIdentifier } from '../material.model';

export type EntityResponseType = HttpResponse<IMaterial>;
export type EntityArrayResponseType = HttpResponse<IMaterial[]>;

@Injectable({ providedIn: 'root' })
export class MaterialService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/materials');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(material: IMaterial): Observable<EntityResponseType> {
    return this.http.post<IMaterial>(this.resourceUrl, material, { observe: 'response' });
  }

  update(material: IMaterial): Observable<EntityResponseType> {
    return this.http.put<IMaterial>(`${this.resourceUrl}/${getMaterialIdentifier(material) as number}`, material, { observe: 'response' });
  }

  partialUpdate(material: IMaterial): Observable<EntityResponseType> {
    return this.http.patch<IMaterial>(`${this.resourceUrl}/${getMaterialIdentifier(material) as number}`, material, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMaterial>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMaterial[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMaterialToCollectionIfMissing(materialCollection: IMaterial[], ...materialsToCheck: (IMaterial | null | undefined)[]): IMaterial[] {
    const materials: IMaterial[] = materialsToCheck.filter(isPresent);
    if (materials.length > 0) {
      const materialCollectionIdentifiers = materialCollection.map(materialItem => getMaterialIdentifier(materialItem)!);
      const materialsToAdd = materials.filter(materialItem => {
        const materialIdentifier = getMaterialIdentifier(materialItem);
        if (materialIdentifier == null || materialCollectionIdentifiers.includes(materialIdentifier)) {
          return false;
        }
        materialCollectionIdentifiers.push(materialIdentifier);
        return true;
      });
      return [...materialsToAdd, ...materialCollection];
    }
    return materialCollection;
  }
}
