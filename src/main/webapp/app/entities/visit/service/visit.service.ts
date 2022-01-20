import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVisit, getVisitIdentifier } from '../visit.model';

export type EntityResponseType = HttpResponse<IVisit>;
export type EntityArrayResponseType = HttpResponse<IVisit[]>;

@Injectable({ providedIn: 'root' })
export class VisitService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(visit: IVisit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visit);
    return this.http
      .post<IVisit>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(visit: IVisit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visit);
    return this.http
      .put<IVisit>(`${this.resourceUrl}/${getVisitIdentifier(visit) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(visit: IVisit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visit);
    return this.http
      .patch<IVisit>(`${this.resourceUrl}/${getVisitIdentifier(visit) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IVisit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IVisit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVisitToCollectionIfMissing(visitCollection: IVisit[], ...visitsToCheck: (IVisit | null | undefined)[]): IVisit[] {
    const visits: IVisit[] = visitsToCheck.filter(isPresent);
    if (visits.length > 0) {
      const visitCollectionIdentifiers = visitCollection.map(visitItem => getVisitIdentifier(visitItem)!);
      const visitsToAdd = visits.filter(visitItem => {
        const visitIdentifier = getVisitIdentifier(visitItem);
        if (visitIdentifier == null || visitCollectionIdentifiers.includes(visitIdentifier)) {
          return false;
        }
        visitCollectionIdentifiers.push(visitIdentifier);
        return true;
      });
      return [...visitsToAdd, ...visitCollection];
    }
    return visitCollection;
  }

  protected convertDateFromClient(visit: IVisit): IVisit {
    return Object.assign({}, visit, {
      arrived: visit.arrived?.isValid() ? visit.arrived.toJSON() : undefined,
      departed: visit.departed?.isValid() ? visit.departed.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.arrived = res.body.arrived ? dayjs(res.body.arrived) : undefined;
      res.body.departed = res.body.departed ? dayjs(res.body.departed) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((visit: IVisit) => {
        visit.arrived = visit.arrived ? dayjs(visit.arrived) : undefined;
        visit.departed = visit.departed ? dayjs(visit.departed) : undefined;
      });
    }
    return res;
  }
}
