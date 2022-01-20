import dayjs from 'dayjs/esm';
import { IMaterial } from 'app/entities/material/material.model';
import { ICertificate } from 'app/entities/certificate/certificate.model';
import { IEvidence } from 'app/entities/evidence/evidence.model';
import { IJob } from 'app/entities/job/job.model';

export interface IVisit {
  id?: number;
  arrived?: dayjs.Dayjs | null;
  departed?: dayjs.Dayjs | null;
  description?: string | null;
  actions?: string | null;
  labour?: number | null;
  materials?: IMaterial[] | null;
  certificates?: ICertificate[] | null;
  evidences?: IEvidence[] | null;
  job?: IJob | null;
}

export class Visit implements IVisit {
  constructor(
    public id?: number,
    public arrived?: dayjs.Dayjs | null,
    public departed?: dayjs.Dayjs | null,
    public description?: string | null,
    public actions?: string | null,
    public labour?: number | null,
    public materials?: IMaterial[] | null,
    public certificates?: ICertificate[] | null,
    public evidences?: IEvidence[] | null,
    public job?: IJob | null
  ) {}
}

export function getVisitIdentifier(visit: IVisit): number | undefined {
  return visit.id;
}
