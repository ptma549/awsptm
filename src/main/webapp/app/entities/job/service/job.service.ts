import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJob, getJobIdentifier } from '../job.model';

export type EntityResponseType = HttpResponse<IJob>;
export type EntityArrayResponseType = HttpResponse<IJob[]>;

@Injectable({ providedIn: 'root' })
export class JobService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/jobs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(job: IJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .post<IJob>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(job: IJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .put<IJob>(`${this.resourceUrl}/${getJobIdentifier(job) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(job: IJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .patch<IJob>(`${this.resourceUrl}/${getJobIdentifier(job) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IJob>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJob[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJobToCollectionIfMissing(jobCollection: IJob[], ...jobsToCheck: (IJob | null | undefined)[]): IJob[] {
    const jobs: IJob[] = jobsToCheck.filter(isPresent);
    if (jobs.length > 0) {
      const jobCollectionIdentifiers = jobCollection.map(jobItem => getJobIdentifier(jobItem)!);
      const jobsToAdd = jobs.filter(jobItem => {
        const jobIdentifier = getJobIdentifier(jobItem);
        if (jobIdentifier == null || jobCollectionIdentifiers.includes(jobIdentifier)) {
          return false;
        }
        jobCollectionIdentifiers.push(jobIdentifier);
        return true;
      });
      return [...jobsToAdd, ...jobCollection];
    }
    return jobCollection;
  }

  protected convertDateFromClient(job: IJob): IJob {
    return Object.assign({}, job, {
      created: job.created?.isValid() ? job.created.toJSON() : undefined,
      assignedAt: job.assignedAt?.isValid() ? job.assignedAt.toJSON() : undefined,
      scheduled: job.scheduled?.isValid() ? job.scheduled.toJSON() : undefined,
      completed: job.completed?.isValid() ? job.completed.toJSON() : undefined,
      updated: job.updated?.isValid() ? job.updated.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.created = res.body.created ? dayjs(res.body.created) : undefined;
      res.body.assignedAt = res.body.assignedAt ? dayjs(res.body.assignedAt) : undefined;
      res.body.scheduled = res.body.scheduled ? dayjs(res.body.scheduled) : undefined;
      res.body.completed = res.body.completed ? dayjs(res.body.completed) : undefined;
      res.body.updated = res.body.updated ? dayjs(res.body.updated) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((job: IJob) => {
        job.created = job.created ? dayjs(job.created) : undefined;
        job.assignedAt = job.assignedAt ? dayjs(job.assignedAt) : undefined;
        job.scheduled = job.scheduled ? dayjs(job.scheduled) : undefined;
        job.completed = job.completed ? dayjs(job.completed) : undefined;
        job.updated = job.updated ? dayjs(job.updated) : undefined;
      });
    }
    return res;
  }
}
