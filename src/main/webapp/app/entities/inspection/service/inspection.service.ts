import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInspection, getInspectionIdentifier } from '../inspection.model';

export type EntityResponseType = HttpResponse<IInspection>;
export type EntityArrayResponseType = HttpResponse<IInspection[]>;

@Injectable({ providedIn: 'root' })
export class InspectionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/inspections');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(inspection: IInspection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inspection);
    return this.http
      .post<IInspection>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(inspection: IInspection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inspection);
    return this.http
      .put<IInspection>(`${this.resourceUrl}/${getInspectionIdentifier(inspection) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(inspection: IInspection): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inspection);
    return this.http
      .patch<IInspection>(`${this.resourceUrl}/${getInspectionIdentifier(inspection) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IInspection>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IInspection[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addInspectionToCollectionIfMissing(
    inspectionCollection: IInspection[],
    ...inspectionsToCheck: (IInspection | null | undefined)[]
  ): IInspection[] {
    const inspections: IInspection[] = inspectionsToCheck.filter(isPresent);
    if (inspections.length > 0) {
      const inspectionCollectionIdentifiers = inspectionCollection.map(inspectionItem => getInspectionIdentifier(inspectionItem)!);
      const inspectionsToAdd = inspections.filter(inspectionItem => {
        const inspectionIdentifier = getInspectionIdentifier(inspectionItem);
        if (inspectionIdentifier == null || inspectionCollectionIdentifiers.includes(inspectionIdentifier)) {
          return false;
        }
        inspectionCollectionIdentifiers.push(inspectionIdentifier);
        return true;
      });
      return [...inspectionsToAdd, ...inspectionCollection];
    }
    return inspectionCollection;
  }

  protected convertDateFromClient(inspection: IInspection): IInspection {
    return Object.assign({}, inspection, {
      created: inspection.created?.isValid() ? inspection.created.toJSON() : undefined,
      updated: inspection.updated?.isValid() ? inspection.updated.toJSON() : undefined,
      start: inspection.start?.isValid() ? inspection.start.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.created = res.body.created ? dayjs(res.body.created) : undefined;
      res.body.updated = res.body.updated ? dayjs(res.body.updated) : undefined;
      res.body.start = res.body.start ? dayjs(res.body.start) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((inspection: IInspection) => {
        inspection.created = inspection.created ? dayjs(inspection.created) : undefined;
        inspection.updated = inspection.updated ? dayjs(inspection.updated) : undefined;
        inspection.start = inspection.start ? dayjs(inspection.start) : undefined;
      });
    }
    return res;
  }
}
