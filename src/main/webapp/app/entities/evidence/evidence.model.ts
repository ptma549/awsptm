import { IVisit } from 'app/entities/visit/visit.model';

export interface IEvidence {
  id?: number;
  name?: string | null;
  url?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  visit?: IVisit | null;
}

export class Evidence implements IEvidence {
  constructor(
    public id?: number,
    public name?: string | null,
    public url?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public visit?: IVisit | null
  ) {}
}

export function getEvidenceIdentifier(evidence: IEvidence): number | undefined {
  return evidence.id;
}
