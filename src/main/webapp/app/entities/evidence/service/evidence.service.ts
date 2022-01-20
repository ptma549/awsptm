import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEvidence, getEvidenceIdentifier } from '../evidence.model';

export type EntityResponseType = HttpResponse<IEvidence>;
export type EntityArrayResponseType = HttpResponse<IEvidence[]>;

@Injectable({ providedIn: 'root' })
export class EvidenceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/evidences');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(evidence: IEvidence): Observable<EntityResponseType> {
    return this.http.post<IEvidence>(this.resourceUrl, evidence, { observe: 'response' });
  }

  update(evidence: IEvidence): Observable<EntityResponseType> {
    return this.http.put<IEvidence>(`${this.resourceUrl}/${getEvidenceIdentifier(evidence) as number}`, evidence, { observe: 'response' });
  }

  partialUpdate(evidence: IEvidence): Observable<EntityResponseType> {
    return this.http.patch<IEvidence>(`${this.resourceUrl}/${getEvidenceIdentifier(evidence) as number}`, evidence, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEvidence>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEvidence[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEvidenceToCollectionIfMissing(evidenceCollection: IEvidence[], ...evidencesToCheck: (IEvidence | null | undefined)[]): IEvidence[] {
    const evidences: IEvidence[] = evidencesToCheck.filter(isPresent);
    if (evidences.length > 0) {
      const evidenceCollectionIdentifiers = evidenceCollection.map(evidenceItem => getEvidenceIdentifier(evidenceItem)!);
      const evidencesToAdd = evidences.filter(evidenceItem => {
        const evidenceIdentifier = getEvidenceIdentifier(evidenceItem);
        if (evidenceIdentifier == null || evidenceCollectionIdentifiers.includes(evidenceIdentifier)) {
          return false;
        }
        evidenceCollectionIdentifiers.push(evidenceIdentifier);
        return true;
      });
      return [...evidencesToAdd, ...evidenceCollection];
    }
    return evidenceCollection;
  }
}
