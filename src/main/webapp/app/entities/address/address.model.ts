import { IGoogleAddress } from 'app/entities/google-address/google-address.model';
import { IJob } from 'app/entities/job/job.model';
import { IInspection } from 'app/entities/inspection/inspection.model';

export interface IAddress {
  id?: number;
  postcode?: string | null;
  number?: string | null;
  position?: string | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  town?: string | null;
  county?: string | null;
  googleAddress?: IGoogleAddress | null;
  jobs?: IJob[] | null;
  inspections?: IInspection[] | null;
}

export class Address implements IAddress {
  constructor(
    public id?: number,
    public postcode?: string | null,
    public number?: string | null,
    public position?: string | null,
    public addressLine1?: string | null,
    public addressLine2?: string | null,
    public town?: string | null,
    public county?: string | null,
    public googleAddress?: IGoogleAddress | null,
    public jobs?: IJob[] | null,
    public inspections?: IInspection[] | null
  ) {}
}

export function getAddressIdentifier(address: IAddress): number | undefined {
  return address.id;
}
