import { IVisit } from 'app/entities/visit/visit.model';

export interface ICertificate {
  id?: number;
  name?: string | null;
  imageContentType?: string | null;
  image?: string | null;
  visit?: IVisit | null;
}

export class Certificate implements ICertificate {
  constructor(
    public id?: number,
    public name?: string | null,
    public imageContentType?: string | null,
    public image?: string | null,
    public visit?: IVisit | null
  ) {}
}

export function getCertificateIdentifier(certificate: ICertificate): number | undefined {
  return certificate.id;
}
