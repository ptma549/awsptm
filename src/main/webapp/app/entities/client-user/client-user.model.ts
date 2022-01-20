import { IUser } from 'app/entities/user/user.model';
import { IJob } from 'app/entities/job/job.model';
import { IInspection } from 'app/entities/inspection/inspection.model';
import { IClientOrganisation } from 'app/entities/client-organisation/client-organisation.model';

export interface IClientUser {
  id?: number;
  landline?: string | null;
  mobile?: string | null;
  user?: IUser | null;
  jobs?: IJob[] | null;
  inspections?: IInspection[] | null;
  client?: IClientOrganisation | null;
}

export class ClientUser implements IClientUser {
  constructor(
    public id?: number,
    public landline?: string | null,
    public mobile?: string | null,
    public user?: IUser | null,
    public jobs?: IJob[] | null,
    public inspections?: IInspection[] | null,
    public client?: IClientOrganisation | null
  ) {}
}

export function getClientUserIdentifier(clientUser: IClientUser): number | undefined {
  return clientUser.id;
}
